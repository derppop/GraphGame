package route

import NetGraphAlgebraDefs.NodeObject
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives.*
import akka.http.scaladsl.server.Route
import service.GraphService
import service.GameStateService.*
import util.JsonFormat
import model.{AdjacentNodesResponse, GameStateResponse, JoinResponse, MoveRequest}
import org.slf4j.LoggerFactory
import util.ResponseGenerator.generateResponse
import scala.annotation.tailrec

object Routes extends SprayJsonSupport with JsonFormat {
  private val logger = LoggerFactory.getLogger(Routes.getClass)

  val routes: Route = concat(
    path("join") {
      post {
        parameter("role") { role =>
          if (role == "cop") {
            if (copExists) {
              complete(StatusCodes.BadRequest, "cop role already full")
            } else {
              copExists = true
              initializeRole("cop")
            }
          } else if (role == "thief") {
            if (thiefExists) {
              complete(StatusCodes.BadRequest, "thief role already full")
            } else {
              thiefExists = true
              initializeRole("thief")
            }
          } else {
            complete(StatusCodes.BadRequest, "Invalid role, please choose \"cop\" or \"thief\" role")
          }
          val response = generateResponse(role).get
          complete(response)
        }
      }
    },
    path("move") {
      post {
        parameters("role", "destination") {(role, destination) =>
          if (copExists && thiefExists) {
            var sourceNode: Option[NodeObject] = None
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
              } else {
                currentThiefNode = Some(newNode.get)
              }
              val response = generateResponse(role)
              complete(response)
            } else {
              complete(StatusCodes.BadRequest, "Node not adjacent to you")
            }
          } else {
            complete(StatusCodes.BadRequest, "Both players must join before moving")
          }
        }
      }
    },
    path("state") {
      get {
        parameter("role") { role =>
          // check if role is valid
          if (role != "cop" && role != "thief") {
            complete(StatusCodes.BadRequest, "Invalid role, please choose \"cop\" or \"thief\" role")
          }

          // check if role joined game
          if (role == "cop" && !copExists) {
            complete(StatusCodes.BadRequest, "You must join before getting state")
          } else if (role == "thief" && !thiefExists) {
            complete(StatusCodes.BadRequest, "You must join before getting state")
          } else {
            val response = generateResponse(role).get
            complete(response)
          }
        }
      }
    },
  )

}
