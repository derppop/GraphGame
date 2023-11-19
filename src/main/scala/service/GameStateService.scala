package service

import NetGraphAlgebraDefs.NodeObject
import org.slf4j.LoggerFactory
import service.GraphService.*

object GameStateService {
  var copExists: Boolean = false
  var thiefExists: Boolean = false
  var currentCopNode: Option[NodeObject] = None
  var copShadowNode: Option[NodeObject] = None
  var currentThiefNode: Option[NodeObject] = None
  var thiefShadowNode: Option[NodeObject] = None
  var winner: Option[String] = None
  var winReason: Option[String] = None

  private val logger = LoggerFactory.getLogger(GameStateService.getClass)

  def initializeRole(role: String): Unit = {
    val randomNodes = getRandomNode
    if (role == "cop") {
      currentCopNode = randomNodes._1
      copShadowNode = randomNodes._2
      logger.info(s"Assigned cop to node ${currentCopNode.get.id}")

      if (isDeadEnd(currentCopNode.get)) { // cop is at dead end
        logger.info("Started cop at dead end, finding new node")
        initializeRole("cop")
      } else if (copShadowNode.isEmpty) { // cop is at invalid node
        logger.info("Started cop at invalid node, finding new node")
        initializeRole("cop")
      }
    } else if (role == "thief") {
      currentThiefNode = randomNodes._1
      thiefShadowNode = randomNodes._2
      logger.info(s"Assigned thief to node ${currentThiefNode.get.id}")

      if (currentThiefNode.get.valuableData) { // thief is on valuable node
        logger.info("Started thief at valuable data, finding new node")
        initializeRole("thief")
      } else if (isDeadEnd(currentThiefNode.get)) { // thief is at dead end
        logger.info("Started thief at dead end, finding new node")
        initializeRole("thief")
      } else if (thiefShadowNode.isEmpty) { // thief is on invalid node
        logger.info("Started thief at invalid node, finding new node")
        initializeRole("thief")
      }
    } else {
      logger.error("Tried initializing invalid role")
    }

    if (currentThiefNode.isDefined && currentCopNode.isDefined && (currentCopNode.get.id == currentThiefNode.get.id)) {
      logger.info("Started players at same node, finding new nodes")
      if (role == "cop") {
        initializeRole("cop")
      } else if (role == "thief") {
        initializeRole("thief")
      }
    }
  }

  def isDeadEnd(node: NodeObject): Boolean = {
    getAdjacentNodes(node).length == 0
  }

  def getSourceNode(role: String): Option[NodeObject] = role match {
    case "cop" => currentCopNode
    case "thief" => currentThiefNode
    case _ => None
  }

  def updatePosition(role: String, newNode: NodeObject): Unit = role match {
    case "cop" => currentCopNode = Some(newNode)
    case "thief" => currentThiefNode = Some(newNode)
  }

  def canMove(sourceNode: NodeObject, destinationNodeId: Int): Option[NodeObject] = {
    val adjacentNodes = getAdjacentNodes(sourceNode)
    adjacentNodes.foreach { node =>
      if (node.id == destinationNodeId) {
        return Some(node)
      }
    }
    None
  }

  def isMoveLegal(sourceNode: NodeObject, destination: String): Boolean = {
    getAdjacentNodes(sourceNode, true).exists(node => node.id == destination.toInt)
  }

  def updateShadowPosition(role: String, destination: String): Unit = {
    var sourceNode = None
    role match {
      case "cop" =>
        getAdjacentNodes(copShadowNode.get, true).foreach(node => if (node.id == destination.toInt) {copShadowNode = Some(node)})
      case "thief" =>
        getAdjacentNodes(thiefShadowNode.get, true).foreach(node => if (node.id == destination.toInt) {thiefShadowNode = Some(node)})
    }
  }

  def restartGame(): Unit = {
    copExists = false
    thiefExists = false
    currentCopNode = None
    copShadowNode = None
    currentThiefNode = None
    thiefShadowNode = None
    winner = None
    winReason = None
  }
}
