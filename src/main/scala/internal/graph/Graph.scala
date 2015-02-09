package internal.graph

/**
 * Trait for specifying a very generic graph interface.
 *
 * @tparam N generic type for Nodes
 */
abstract class Graph[N] {

  /**
   * A simple representation of a Node containing a value of type N.
   */
  case class Node(value: N)

  /**
   * A simple representation of an Edge connecting two Nodes of type [[NodeT]].
   */
  case class Edge(left: NodeT, right: NodeT)

  /**
   * Type for [[Node]]s.
   */
  type NodeT = Node

  /**
   * Type for [[Edge]]s.
   */
  type EdgeT = Edge

  /**
   * @return a List of all [[Node]]s
   */
  def nodes: List[NodeT]

  /**
   * @return a List of all [[Edge]]s
   */
  def edges: List[EdgeT]

  /**
   * Add a [[Node]] to the graph.
   *
   * @param node
   * @return the added [[Node]]
   */
  def addNode(node: NodeT): NodeT

  /**
   * Remove a [[Node]] from the graph.
   *
   * @param node
   * @return the removed [[Node]]
   */
  def removeNode(node: NodeT): NodeT

  /**
   * Add a [[Edge]] to the graph.
   *
   * @param edge
   * @return the added [[Edge]]
   */
  def addEdge(edge: EdgeT): EdgeT

  /**
   * Remove a [[Edge]] from the graph.
   *
   * @param edge
   * @return the removed [[Edge]]
   */
  def removeEdge(edge: EdgeT): EdgeT
}