package util

import NetGraphAlgebraDefs.NodeObject
import model.{AdjacentNodesResponse, GameStateResponse}
import service.GameStateService.{currentCopNode, currentThiefNode}
import service.GraphService

object ResponseGenerator {
  def generateResponse(role: String): Option[GameStateResponse] = {
    if (role == "cop") {
      val adjacentNodes: Array[NodeObject] = GraphService.getAdjacentNodes(currentCopNode.get)
      val adjacentNodesResponse: Array[AdjacentNodesResponse] = adjacentNodes.map(node => {
        AdjacentNodesResponse(node.id)
      })
      Some(GameStateResponse(currentCopNode.get.id, adjacentNodesResponse, "cop"))
    } else if (role == "thief") {
      val adjacentNodes: Array[NodeObject] = GraphService.getAdjacentNodes(currentThiefNode.get)
      val adjacentNodesResponse: Array[AdjacentNodesResponse] = adjacentNodes.map(node => {
        AdjacentNodesResponse(node.id)
      })
      Some(GameStateResponse(currentThiefNode.get.id, adjacentNodesResponse, "thief"))
    } else {
      None
    }
  }
}
