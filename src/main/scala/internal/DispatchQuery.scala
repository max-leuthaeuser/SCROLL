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
}

class DispatchQuery(
  from: Any => Boolean,
  to: Any => Boolean,
  through: Any => Boolean,
  bypassing: Any => Boolean
  )