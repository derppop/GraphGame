package service

import NetGraphAlgebraDefs.NodeObject
import org.slf4j.LoggerFactory

object GameStateService {
  var copExists: Boolean = false
  var thiefExists: Boolean = false
  var currentCopNode: Option[NodeObject] = None
  var copShadowNode: Option[NodeObject] = None
  var currentThiefNode: Option[NodeObject] = None
  var thiefShadowNode: Option[NodeObject] = None
  var winner: Option[String] = None

  private val logger = LoggerFactory.getLogger(GameStateService.getClass)

  def initializeRole(role: String): Unit = {
    val randomNodes = GraphService.getRandomNode
    if (role == "cop") {
      currentCopNode = randomNodes._1
      copShadowNode = randomNodes._2
      logger.info(s"Assigned cop to node ${currentCopNode.get.id}")

      if (GraphService.getAdjacentNodes(currentCopNode.get).length == 0) { // cop is at dead end
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
      } else if (GraphService.getAdjacentNodes(currentThiefNode.get).length == 0) { // thief is at dead end
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

}
