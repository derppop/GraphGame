package util

import model.{MoveRequest, AdjacentNodesResponse, GameStateResponse}
import spray.json.DefaultJsonProtocol
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json._

trait JsonFormat extends DefaultJsonProtocol {
  implicit val moveRequestFormat: RootJsonFormat[MoveRequest] = jsonFormat2(MoveRequest.apply)
  implicit val adjacentNodesResponseFormat: RootJsonFormat[AdjacentNodesResponse] = jsonFormat1(AdjacentNodesResponse.apply)
  implicit val gameStateResponseFormat: RootJsonFormat[GameStateResponse] = jsonFormat3(GameStateResponse.apply)
}
