package monkey.test

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestActors, TestKit}
import monkey.actor.QueryPassingMonkeys
import monkey.{EastToWest, WestToEast}
import monkey.test.system.TestMonkeySystem
import org.scalatest.WordSpecLike
import org.scalatest.Matchers
import org.scalatest.BeforeAndAfterAll

class MySpec() extends TestKit(ActorSystem()) with ImplicitSender
    with WordSpecLike with Matchers with BeforeAndAfterAll {

    override def afterAll {
        TestKit.shutdownActorSystem(system)
    }

    "If two monkeys are enqueued at the same time" must {
        "be dequeued in around 10 seconds" in {
            TestMonkeySystem.parent ! Seq((0, EastToWest), (0, WestToEast))
            Thread.sleep(3000)
            TestMonkeySystem.monitor ! QueryPassingMonkeys
            expectMsg("hello world")
        }

    }

    override protected def beforeAll() = TestMonkeySystem.set(system)
}

