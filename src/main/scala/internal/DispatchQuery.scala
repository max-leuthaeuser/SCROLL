package internal

object DispatchQuery extends ReflectiveHelper
{
  def From(f: Any => Boolean) = new
    {
      def To(t: Any => Boolean) = new
        {
          def Through(th: Any => Boolean) = new
            {
              def Bypassing(b: Any => Boolean): DispatchQuery = new DispatchQuery(f, t, th, b)
            }
        }
    }

  def empty: DispatchQuery = new DispatchQuery(empty = true)
}

class DispatchQuery(
  from: Any => Boolean = _ => true,
  to: Any => Boolean = _ => true,
  through: Any => Boolean = _ => true,
  bypassing: Any => Boolean = _ => true,
  private val empty: Boolean = false
  )
{
  def isEmpty: Boolean = empty
}