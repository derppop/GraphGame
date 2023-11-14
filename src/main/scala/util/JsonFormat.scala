package util

import model.{MoveRequest, StateResponse, AdjacentNodesResponse}
import spray.json.DefaultJsonProtocol
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json._


trait JsonFormat extends DefaultJsonProtocol {
  implicit val moveRequestFormat: RootJsonFormat[MoveRequest] = jsonFormat2(MoveRequest)
  implicit val stateResponseFormat: RootJsonFormat[StateResponse] = jsonFormat2(StateResponse)
  implicit val adjacentNodesFormat: RootJsonFormat[AdjacentNodesResponse] = jsonFormat1(AdjacentNodesResponse)
}
