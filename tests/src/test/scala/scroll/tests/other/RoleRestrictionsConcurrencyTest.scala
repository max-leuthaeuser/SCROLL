package scroll.tests.other

import scroll.internal.support.impl.RoleRestrictions
import scroll.tests.AbstractSCROLLTest
import scroll.tests.mocks.CoreA

import java.util.concurrent.CountDownLatch
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration.DurationInt

class RoleRestrictionsConcurrencyTest extends AbstractSCROLLTest {

  class RoleOne { def one(): Int = 1 }

  class RoleTwo { def two(): Int = 2 }

  class RoleThree { def three(): Int = 3 }

  class RoleFour { def four(): Int = 4 }

  class RoleFive { def five(): Int = 5 }

  class RoleSix { def six(): Int = 6 }

  private def registerRestrictionsConcurrently(restrictions: RoleRestrictions): Unit = {
    val start         = new CountDownLatch(1)
    val registrations = Seq[() => Unit](
      () => restrictions.addRoleRestriction[CoreA, RoleOne](),
      () => restrictions.addRoleRestriction[CoreA, RoleTwo](),
      () => restrictions.addRoleRestriction[CoreA, RoleThree](),
      () => restrictions.addRoleRestriction[CoreA, RoleFour](),
      () => restrictions.addRoleRestriction[CoreA, RoleFive](),
      () => restrictions.addRoleRestriction[CoreA, RoleSix]()
    )

    val tasks = registrations.map { register =>
      Future {
        start.await()
        register()
      }
    }

    start.countDown()
    Await.result(Future.sequence(tasks), 10.seconds)
  }

  test("Concurrent restriction updates do not lose allowed role types") {
    val player = new CoreA()

    (1 to 50).foreach { _ =>
      val restrictions = new RoleRestrictions()

      registerRestrictionsConcurrently(restrictions)

      noException should be thrownBy restrictions.validate(player, new RoleOne())
      noException should be thrownBy restrictions.validate(player, new RoleTwo())
      noException should be thrownBy restrictions.validate(player, new RoleThree())
      noException should be thrownBy restrictions.validate(player, new RoleFour())
      noException should be thrownBy restrictions.validate(player, new RoleFive())
      noException should be thrownBy restrictions.validate(player, new RoleSix())
    }
  }

}
