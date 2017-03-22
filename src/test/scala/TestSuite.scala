package monkey.test

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import monkey.message.{Done, EastToWest, OnTheRope, WestToEast}
import monkey.test.system.TestMonkeySystem
import org.scalatest.WordSpecLike
import org.scalatest.Matchers
import org.scalatest.BeforeAndAfterAll

import scala.concurrent.duration._

class TestSuite() extends TestKit(ActorSystem()) with ImplicitSender
    with WordSpecLike with Matchers with BeforeAndAfterAll {

    override def afterAll {
        TestKit.shutdownActorSystem(system)
    }

    "A lonely monkey" must {
        "be dequeued in around 5 seconds" in {
            TestMonkeySystem.reset(system, this.testActor)
            TestMonkeySystem.parent ! EastToWest
            expectMsg(EastToWest)
            expectNoMsg(1.second)
            expectMsg(0.1.seconds, OnTheRope)
            expectNoMsg(4.second)
            expectMsg(0.1.seconds, Done)
        }
    }


    "Two monkeys crossing and arriving at the same time" must {
        "be dequeued" in {
            TestMonkeySystem.reset(system, this.testActor)
            TestMonkeySystem.parent ! EastToWest
            TestMonkeySystem.parent ! WestToEast
            expectMsgAnyOf(EastToWest, WestToEast)
            expectMsgAnyOf(EastToWest, WestToEast)
            expectMsg(1.1.seconds, OnTheRope)
            expectNoMsg(4.second)
            expectMsg(0.1.seconds, Done)
            expectNoMsg(1.second)
            expectMsg(0.1.seconds, OnTheRope)
            expectNoMsg(4.second)
            expectMsg(0.1.seconds, Done)
            assert(TestMonkeySystem.monitor.underlyingActor.passing.isEmpty, "Monkey has not passed.")
            assert(TestMonkeySystem.monitor.underlyingActor.fromEast.isEmpty, "Monkey from East is still enqueued.")
            assert(TestMonkeySystem.monitor.underlyingActor.fromWest.isEmpty, "Monkey from West is still enqueued.")
        }
    }

    "Two monkeys crossing and arriving at different time" must {
        "be dequeued" in {
            TestMonkeySystem.reset(system, this.testActor)
            TestMonkeySystem.parent ! EastToWest
            Thread.sleep(200)
            TestMonkeySystem.parent ! WestToEast
            expectMsg(EastToWest)
            expectMsg(WestToEast)
            expectMsg(1.seconds, OnTheRope)
            expectNoMsg(4.second)
            expectMsg(0.1.seconds, Done)
            expectNoMsg(1.second)
            expectMsg(0.1.seconds, OnTheRope)
            expectNoMsg(4.second)
            expectMsg(0.1.seconds, Done)
            assert(TestMonkeySystem.monitor.underlyingActor.passing.isEmpty, "Monkey has not passed.")
            assert(TestMonkeySystem.monitor.underlyingActor.fromEast.isEmpty, "Monkey from East is still enqueued.")
            assert(TestMonkeySystem.monitor.underlyingActor.fromWest.isEmpty, "Monkey from West is still enqueued.")
        }
    }

    "Two monkeys going in the same direction and arriving at the same time" must {
        "be dequeued" in {
            TestMonkeySystem.reset(system, this.testActor)
            TestMonkeySystem.parent ! EastToWest
            TestMonkeySystem.parent ! EastToWest
            expectMsg(EastToWest)
            expectMsg(EastToWest)
            expectMsg(1.1.seconds, OnTheRope)
            expectNoMsg(1.second)
            expectMsg(0.1.seconds, OnTheRope)
            expectMsg(3.seconds, Done)
            expectNoMsg(1.second)
            expectMsg(0.1.seconds, Done)
            assert(TestMonkeySystem.monitor.underlyingActor.fromEast.isEmpty, "Monkey is still enqueued.")
            assert(TestMonkeySystem.monitor.underlyingActor.passing.isEmpty, "Monkey has not passed.")
        }
    }

    "Three monkeys going in different directions" must {
        "be dequeued" in {
            TestMonkeySystem.reset(system, this.testActor)
            TestMonkeySystem.parent ! EastToWest
            Thread.sleep(200)
            TestMonkeySystem.parent ! WestToEast
            Thread.sleep(200)
            TestMonkeySystem.parent ! EastToWest
            expectMsg(EastToWest)
            expectMsg(WestToEast)
            expectMsg(EastToWest)
            expectMsg(OnTheRope)
            assert(TestMonkeySystem.monitor.underlyingActor.fromWest.size == 1, "Monkey from West did not wait.")
            assert(TestMonkeySystem.monitor.underlyingActor.fromEast.size == 1, "Monkey from East did not wait.")
            expectMsg(4.1.seconds, Done)
            expectMsg(OnTheRope)
            assert(TestMonkeySystem.monitor.underlyingActor.fromWest.isEmpty, "Monkey from West is still enqueued.")
            assert(TestMonkeySystem.monitor.underlyingActor.fromEast.size == 1, "Monkey from East did not wait.")
            expectMsg(4.1.seconds, Done)
            expectMsg(OnTheRope)
            assert(TestMonkeySystem.monitor.underlyingActor.fromWest.isEmpty, "Monkey from West is still enqueued.")
            assert(TestMonkeySystem.monitor.underlyingActor.fromEast.isEmpty, "Monkey from East is still enqueued.")
            expectMsg(4.1.seconds, Done)
            assert(TestMonkeySystem.monitor.underlyingActor.passing.isEmpty, "Monkey has not passed.")
            assert(TestMonkeySystem.monitor.underlyingActor.fromEast.isEmpty, "Monkey from East is still enqueued.")
            assert(TestMonkeySystem.monitor.underlyingActor.fromWest.isEmpty, "Monkey from West is still enqueued.")

        }
    }
}

