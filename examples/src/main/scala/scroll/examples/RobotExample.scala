package scroll.examples

import scroll.examples.RobotExample.ActorView.DriveableRole
import scroll.examples.RobotExample.BehavioralView.ServiceRole
import scroll.examples.RobotExample.NavigationView.NavigationRole
import scroll.examples.RobotExample.SensorView.ObservingEnvironmentRole
import scroll.internal.compartment.impl.Compartment

object RobotExample {

  @main def runRobotExample(): Unit = {
    val _ = new Compartment {
      val myRobot =
        Robot("Pete") play ServiceRole() play NavigationRole() play ObservingEnvironmentRole() play DriveableRole()
      BehavioralView.compartmentRelations.partOf(this)
      myRobot.move()
    }
  }

  case class Robot(name: String)

  object BehavioralView extends Compartment {

    case class ServiceRole() {

      def move(): Unit = {
        val name: String     = (+this).name()
        val target: String   = (+this).getTarget()
        val sensorValue: Int = (+this).readSensor()
        val actor: String    = (+this).getActor()
        println(s"I am $name and moving to the $target with my $actor w.r.t. sensor value of $sensorValue.")
      }

    }

  }

  object NavigationView extends Compartment {

    case class NavigationRole() {
      def getTarget: String = "kitchen"
    }

  }

  object SensorView extends Compartment {

    case class ObservingEnvironmentRole() {
      def readSensor: Int = 100
    }

  }

  object ActorView extends Compartment {

    case class DriveableRole() {
      def getActor: String = "wheels"
    }

  }

}
