package service

import NetGraphAlgebraDefs.{GraphPerturbationAlgebra, NetGraph, NetModelAlgebra, NodeObject}
import scala.util.Random
import scala.jdk.CollectionConverters._

object GraphService {
  // generate original and perturbed graph
  // provide methods for getting adjacent nodes of thief and cop
  // provide methods for moving thief and cop
  val originalGraph: NetGraph = NetModelAlgebra().get
  val perturbedGraph: NetGraph = GraphPerturbationAlgebra(originalGraph.copy)._1

  val originalNodes: Array[NodeObject] = originalGraph.sm.nodes().asScala.toArray
  val perturbedNodes: Array[NodeObject] = perturbedGraph.sm.nodes().asScala.toArray

  def getRandomNode: NodeObject = {
    perturbedNodes(Random.nextInt(perturbedNodes.length))
  }

  def getAdjacentNodes(node: NodeObject): Array[NodeObject] = {
    perturbedGraph.sm.adjacentNodes(node).asScala.toArray
  }

  def canMove(sourceNode: NodeObject, destinationNodeId: Int): Option[NodeObject] = {
    val adjacentNodes = getAdjacentNodes(sourceNode)
    adjacentNodes.foreach{ node =>
      if (node.id == destinationNodeId) {
        return Some(node)
      }
    }
    None
  }
  def gameEnded(thiefNode: NodeObject, copNode: NodeObject): Boolean = {
    thiefNode == copNode
  }
}
