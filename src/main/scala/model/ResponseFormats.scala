package model
case class AdjacentNodesResponse(id: Int)
case class GameStateResponse(currentNode: Int, adjacentNodes: Array[AdjacentNodesResponse], role: String)