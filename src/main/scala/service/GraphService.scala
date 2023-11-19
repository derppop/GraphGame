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

  def getRandomNode: (Option[NodeObject], Option[NodeObject]) = {
    val node: Option[NodeObject] = Some(perturbedNodes(Random.nextInt(perturbedNodes.length)))
    val shadowNode: Option[NodeObject] = originalNodes.find(shadowNode => shadowNode.id == node.get.id)
    (node, shadowNode)
  }

  def getAdjacentNodes(node: NodeObject, checkOriginalGraph: Boolean = false): Array[NodeObject] = {
    if (checkOriginalGraph) {
      originalGraph.sm.successors(node).asScala.toArray
    } else {
      perturbedGraph.sm.successors(node).asScala.toArray
    }
  }

}
