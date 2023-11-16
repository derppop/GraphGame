package util

import model.{MoveRequest, JoinResponse, AdjacentNodesResponse, GameStateResponse}
import spray.json.DefaultJsonProtocol
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json._


trait JsonFormat extends DefaultJsonProtocol {
  implicit val moveRequestFormat: RootJsonFormat[MoveRequest] = jsonFormat2(MoveRequest)
  implicit val joinResponseFormat: RootJsonFormat[JoinResponse] = jsonFormat2(JoinResponse)
  implicit val adjacentNodesResponseFormat: RootJsonFormat[AdjacentNodesResponse] = jsonFormat1(AdjacentNodesResponse)
  implicit val gameStateResponseFormat: RootJsonFormat[GameStateResponse] = jsonFormat2(GameStateResponse)
}
