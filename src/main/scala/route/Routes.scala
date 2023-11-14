package route

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Directives.*
import akka.http.scaladsl.server.Route
import service.GraphService
import util.JsonFormat
import model.MoveRequest

object Routes extends SprayJsonSupport with JsonFormat {

  private var copId: Option[String] = None
  private var thiefId: Option[String] = None
  // store info about users like the player id for thief and cop
  // provide routes for setup, getting adjacent nodes, and moving
  // based on player id decide if cop or thief should move
  val routes: Route = concat(
    path("join") {
      post {
        parameter("playerId") { playerId =>
          // if cop is 0 set copId to player id and return role of user as response
          // if cop is set and thief is 0, set thiefId to playerId and return role of user as response
          // in both cases, pick a random node in graph for user to start on
          println(s"Player ID: ${playerId}")
          if (copId.isEmpty) {
            copId = Some(playerId)
            complete(s"You are the cop")
          } else if (thiefId.isEmpty) {
            thiefId = Some(playerId)
            complete(s"you are the thief")
          } else {
            complete("Game is full")
          }
        }
      }
    },
    path("move") {
      post {
        entity(as[MoveRequest]) {moveRequest =>
          // move player to adjacent node with same id specified in request
          // if node is not adjacent, response is an error
          println(s"playerId: ${moveRequest.playerId} destinationNode: ${moveRequest.destinationNode}")
          complete("")
        }
      }
    },
    path("getAdjacentNodes") {
      get {
        parameter("playerId") { playerId =>
          // get adjacent nodes from appropriate node and return list as a response
          println(s"Player ID: ${playerId}")
          complete("")
        }
      }
    },
  )

}
