package internal.graph

/**
 * Generic graph adapter interface for integrating other
 * graph implementations later on. See [[RoleGraph]].
 *
 * @param <N> Nodes
 * @param <E> Edges
 */
trait GraphAdapter[N, E] {
  /**
   * @param node
   * @return a List of all edges the given nodes has as incoming edges
   */
  def getInEdges(node: N): List[E]

  /**
   * @param node
   * @param filter
   * @return a List of all edges the given nodes has as incoming edges with the given filter applied
   */
  def getInEdges(node: N, filter: E => Boolean): List[E]

  /**
   * @param node
   * @param filter
   * @return a List of all edges the given nodes has as outgoing edges with the given filter applied
   */
  def getOutEdges(node: N): List[E]

  /**
   * @param node
   * @param filter
   * @return a List of all edges the given nodes has as outgoing edges with the given filter applied
   */
  def getOutEdges(node: N, filter: E => Boolean): List[E]

  /**
   * @param edge
   * @return the Node the given edge has a start node
   */
  def getStart(edge: E): N

  /**
   * @param edge
   * @return the Node the given edge has a end node
   */
  def getEnd(edge: E): N

  /**
   * @return a List of all Edges
   */
  def getEdges: List[E]

  /**
   * @return a List of all Edges with the given filter applied
   */
  def getEdges(filter: E => Boolean): List[E]

  /**
   * @return a List of all Nodes
   */
  def getNodes: List[N]

  /**
   * @return a List of all Nodes with the given filter applied
   */
  def getNodes(filter: N => Boolean): List[N]
}