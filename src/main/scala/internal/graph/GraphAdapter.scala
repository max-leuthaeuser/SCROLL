package internal.graph

// N: Nodes
// E: Edges
trait GraphAdapter[N, E] {
  def getInEdges(node: N): List[E]
  def getInEdges(node: N, filter: E => Boolean): List[E]

  def getOutEdges(node: N): List[E]
  def getOutEdges(node: N, filter: E => Boolean): List[E]

  def getStart(edge: E): N
  def getEnd(edge: E): N

  def getEdges: List[E]
  def getEdges(filter: E => Boolean): List[E]

  def getNodes: List[N]
  def getNodes(filter: N => Boolean): List[N]
}