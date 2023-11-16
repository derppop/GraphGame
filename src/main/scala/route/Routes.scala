package route

import NetGraphAlgebraDefs.NodeObject
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives.*
import akka.http.scaladsl.server.Route
import service.GraphService
import util.JsonFormat
import model.{AdjacentNodesResponse, GameStateResponse, JoinResponse, MoveRequest}
import org.slf4j.LoggerFactory

object Routes extends SprayJsonSupport with JsonFormat {
  private val logger = LoggerFactory.getLogger(Routes.getClass)

  // game state vars
  private var copExists: Boolean = false
  private var thiefExists: Boolean = false
  private var currentCopNode: Option[NodeObject] = None
  private var currentThiefNode: Option[NodeObject] = None
  private var winner: Option[String] = None

  // store info about users like the player id for thief and cop
  // provide routes for setup, getting adjacent nodes, and moving
  // based on player id decide if cop or thief should move
  val routes: Route = concat(
    path("join") {
      post {
        parameter("role") { role =>
          if (role == "cop") {
            if (copExists) {
              complete(StatusCodes.BadRequest, "cop role already full")
            } else {
              logger.info(s"Assigned cop role")
              copExists = true
              currentCopNode = Some(GraphService.getRandomNode)
              val response: JoinResponse = JoinResponse("cop", currentCopNode.get.id)
              complete(response)
            }
          } else if (role == "thief") {
            if (thiefExists) {
              complete(StatusCodes.BadRequest, "thief role already full")
            } else {
              logger.info(s"Assigned thief role")
              thiefExists = true
              currentThiefNode = Some(GraphService.getRandomNode)
              val response: JoinResponse = JoinResponse("thief", currentThiefNode.get.id)
              complete(response)
            }
          } else {
            complete(StatusCodes.BadRequest, "Invalid role, please choose \"cop\" or \"thief\" role")
          }
        }
      }
    },
    path("move") {
      post {
        parameters("role", "destination") {(role, destination) =>
          // move player to adjacent node with same id specified in request
          // if node is not adjacent, response is an error
          var sourceNode:Option[NodeObject] = None
          if (role == "cop") {
            sourceNode = Some(currentCopNode.get)
          } else if (role == "thief") {
            sourceNode = Some(currentThiefNode.get)
          } else {
            complete(StatusCodes.BadRequest, "Invalid role, please choose \"cop\" or \"thief\" role")
          }
          val newNode: Option[NodeObject] = GraphService.canMove(sourceNode.get, destination.toInt)
          if (newNode.isDefined) {
            if (role == "cop") {
              currentCopNode = Some(newNode.get)
              //if copNode == thiefNode, cop wins
              // if copNode == fakeNode, cop loses
              val response: JoinResponse = JoinResponse("cop", currentCopNode.get.id)
              complete(response)
            } else {
              currentThiefNode = Some(newNode.get)
              // if thiefNode has valuableData, thief wins
              // if thiefNode == fakeNode, thief loses
              val response: JoinResponse = JoinResponse("thief", currentThiefNode.get.id)
              complete(response)
            }
          } else {
            complete(StatusCodes.BadRequest, "Node not adjacent to you")
          }
        }
      }
    },
    path("state") {
      get {
        parameter("role") { role =>
          // get adjacent nodes from appropriate node and return list as a response
          if (role == "cop") {
            val adjacentNodes: Array[NodeObject] = GraphService.getAdjacentNodes(currentCopNode.get)
            val adjacentNodesResponse: Array[AdjacentNodesResponse] = adjacentNodes.map(node => {AdjacentNodesResponse(node.id)})
            val response: GameStateResponse = GameStateResponse(currentCopNode.get.id, adjacentNodesResponse)
            complete(response)
          } else if (role == "thief") {
            val adjacentNodes: Array[NodeObject] = GraphService.getAdjacentNodes(currentThiefNode.get)
            val adjacentNodesResponse: Array[AdjacentNodesResponse] = adjacentNodes.map(node => {AdjacentNodesResponse(node.id)})
            val response: GameStateResponse = GameStateResponse(currentThiefNode.get.id, adjacentNodesResponse)
            complete(response)
          } else {
            complete(StatusCodes.BadRequest, "Invalid role, please choose \"cop\" or \"thief\" role")
          }
        }
      }
    },
  )

}
