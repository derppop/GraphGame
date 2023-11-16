package model

case class JoinResponse(role: String, location: Int)
case class AdjacentNodesResponse(id: Int)
case class GameStateResponse(currentNode: Int, adjacentNodes: Array[AdjacentNodesResponse])