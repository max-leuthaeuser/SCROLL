package internal.graph

abstract class Graph[N] {

  case class Node(value: N)

  case class Edge(left: NodeT, right: NodeT)

  type NodeT = Node
  type EdgeT = Edge

  def nodes: List[NodeT]
  def edges: List[EdgeT]

  def addNode(node: NodeT): NodeT
  def removeNode(node: NodeT): NodeT

  def addEdge(edge: EdgeT): EdgeT
  def removeEdge(edge: EdgeT): EdgeT
}