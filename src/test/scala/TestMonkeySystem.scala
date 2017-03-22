package monkey.test.system

import akka.actor.{ActorRef, ActorSystem}
import akka.testkit.TestActorRef
import monkey.actor.{MonkeyMonitor, MonkeyParent}

/**
  * This class wraps a given Actor System and sets it for testing. It allows to reset the system so it could be used in
  * different tests.
  */
class TestMonkeySystem(val system: ActorSystem, val ft: ActorRef) {

    var monitor: TestActorRef[MonkeyMonitor] = _
    var parent: ActorRef = _

    def reset(): Unit = {
        if (monitor != null) system stop monitor
        if (parent != null) system stop parent
        monitor = TestActorRef.create(system, MonkeyMonitor.props(ft))
        parent = system.actorOf(MonkeyParent.props(monitor))
    }

}
