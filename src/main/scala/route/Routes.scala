package route

import NetGraphAlgebraDefs.NodeObject
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives.*
import akka.http.scaladsl.server.Route
import service.GraphService
import service.GameStateService.*
import util.JsonFormat
import model.{AdjacentNodesResponse, GameStateResponse, MoveRequest}
import org.slf4j.LoggerFactory
import util.ResponseGenerator.generateResponse
import scala.annotation.tailrec
import service.GraphService.*

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
            if (winner.isEmpty) {
              val sourceNode = getSourceNode(role)
              val destinationNode = canMove(sourceNode.get, destination.toInt)
              destinationNode match {
                case None =>
                  complete(StatusCodes.BadRequest, "Node is not adjacent to you")
                case Some(node) =>
                  updatePosition(role, node)

                  if (role == "thief" && currentThiefNode.get.valuableData) { // thief finds valuable data
                    logger.info("Thief won the game, they landed on valuable data")
                    winner = Some("thief")
                    winReason = Some("the thief found the valuable data!")
                    complete("You win! You found the valuable data")
                  } else if (role == "thief" && isDeadEnd(node)){ // thief ran into dead end
                    logger.info("Thief lost the game, they ran into a dead end")
                    winner = Some("cop")
                    winReason = Some("the thief ran into a dead end!")
                    complete("You lose! You ran into a dead end")
                  } else if (role == "cop" && isDeadEnd(node)) { // cop ran into dead end
                    logger.info("Cop lost the game, they ran into a dead end")
                    winner = Some("thief")
                    winReason = Some("the cop ran into a dead end!!")
                    complete("You lose! You ran into a dead end")
                  } else if (role == "cop" && (currentThiefNode.get.id == currentCopNode.get.id)) { // cop caught thief
                    logger.info("Cop won the game, they caught the thief")
                    winner = Some("cop")
                    winReason = Some("the cop caught the thief!")
                    complete("You win! You caught the thief")
                  } else if (role == "cop" && !isMoveLegal(sourceNode.get, destination)) { // perturbed and original graph differ
                    logger.info("Cop lost the game, they made a move not possible in original graph")
                    winner = Some("thief")
                    winReason = Some("the cop stepped in a trap!")
                    complete("You Lose! You stepped in a trap")
                  } else if (role == "thief" && !isMoveLegal(sourceNode.get, destination)) { // perturbed and original graph differ
                    logger.info("Thief lost the game, they made a move not possible in original graph")
                    winner = Some("cop")
                    winReason = Some("the thief stepped in a trap!")
                    complete("You Lose! You stepped in a trap")
                  }else {
                    val response = generateResponse(role)
                    complete(response)
                  }
              }
          } else {
              complete(s"Game is over, ${winner.get} won because ${winReason.get}")
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
          if (winner.isEmpty) {
            // check if role joined game
            if (role == "cop" && !copExists) {
              complete(StatusCodes.BadRequest, "You must join before getting state")
            } else if (role == "thief" && !thiefExists) {
              complete(StatusCodes.BadRequest, "You must join before getting state")
            } else {
              val response = generateResponse(role).get
              complete(response)
            }
          } else {
            complete(s"Game is over, ${winner.get} won because ${winReason.get}")
          }
        }
      }
    },
    path("restart") {
      post {
        restartGame()
        complete("Restarted the game, please rejoin")
      }
    },
  )

}
