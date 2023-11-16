package route

import NetGraphAlgebraDefs.NodeObject
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives.*
import akka.http.scaladsl.server.Route
import service.GraphService
import util.JsonFormat
import model.{MoveRequest, StateResponse, AdjacentNodesResponse}
import org.slf4j.LoggerFactory

object Routes extends SprayJsonSupport with JsonFormat {
  private val logger = LoggerFactory.getLogger(Routes.getClass)
  private var copId: Option[String] = None
  private var thiefId: Option[String] = None
  private var currentCopNode: Option[NodeObject] = None
  private var currentThiefNode: Option[NodeObject] = None
  // store info about users like the player id for thief and cop
  // provide routes for setup, getting adjacent nodes, and moving
  // based on player id decide if cop or thief should move
  val routes: Route = concat(
    path("join") {
      post {
        parameter("playerId") { playerId =>
          if (copId.isEmpty) {
            logger.info(s"Assigned cop role to player id: ${playerId}")
            copId = Some(playerId)
            currentCopNode = Some(GraphService.getRandomNode)
            val response: StateResponse = StateResponse("Cop", currentCopNode.get.id)
            complete(response)
          } else if (thiefId.isEmpty) {
            if (copId.get == playerId) {
              complete(StatusCodes.BadRequest, "Id is not unique")
            } else {
              logger.info(s"Assigned thief role to player id: ${playerId}")
              thiefId = Some(playerId)
              currentThiefNode = Some(GraphService.getRandomNode)
              val response: StateResponse = StateResponse("Thief", currentThiefNode.get.id)
              complete(response)
            }
          } else {
            complete(StatusCodes.Forbidden, "Game is full")
          }
        }
      }
    },
    path("move") {
      post {
        entity(as[MoveRequest]) {moveRequest =>
          // move player to adjacent node with same id specified in request
          // if node is not adjacent, response is an error
          var sourceNode:Option[NodeObject] = None
          if (moveRequest.playerId == copId.get) {
            sourceNode = Some(currentCopNode.get)
          } else if (moveRequest.playerId == thiefId.get) {
            sourceNode = Some(currentThiefNode.get)
          } else {
            complete(StatusCodes.BadRequest, "User Id does not exist")
          }
          val newNode: Option[NodeObject] = GraphService.canMove(sourceNode.get, moveRequest.destinationNode.toInt)
          if (newNode.isDefined) {
            if (moveRequest.playerId == copId.get) {
              currentCopNode = Some(newNode.get)
              //if copNode == thiefNode, cop wins
              // if copNode == fakeNode, cop loses
              val response: StateResponse = StateResponse("Cop", currentCopNode.get.id)
              complete(response)
            } else if (moveRequest.playerId == thiefId.get) {
              currentThiefNode = Some(newNode.get)
              // if thiefNode has valuableData, thief wins
              // if thiefNode == fakeNode, thief loses
              val response: StateResponse = StateResponse("Thief", currentThiefNode.get.id)
              complete(response)
            } else {
              complete("")
            }
          } else {
            complete(StatusCodes.BadRequest, "Node not adjacent to you")
          }
        }
      }
    },
    path("getAdjacentNodes") {
      get {
        parameter("playerId") { playerId =>
          // get adjacent nodes from appropriate node and return list as a response
          println(s"Player ID: ${playerId}")
          if (playerId == copId.get) {
            val adjacentNodes: Array[NodeObject] = GraphService.getAdjacentNodes(currentCopNode.get)
            val response: Array[AdjacentNodesResponse] = adjacentNodes.map(node => {AdjacentNodesResponse(node.id)})
            complete(response)
          } else if (playerId == thiefId.get) {
            val adjacentNodes: Array[NodeObject] = GraphService.getAdjacentNodes(currentThiefNode.get)
            val response: Array[AdjacentNodesResponse] = adjacentNodes.map(node => {
              AdjacentNodesResponse(node.id)
            })
            complete(response)
          } else {
            complete("")
          }
        }
      }
    },
  )

}
