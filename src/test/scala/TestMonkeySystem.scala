package monkey.test.system

import akka.actor.{ActorRef, ActorSystem}
import akka.testkit.TestActorRef
import monkey.actor.{MonkeyMonitor, MonkeyParent}

/**
  * Created by asantuy on 16/03/2017.
  */
object TestMonkeySystem {

    var monitor: TestActorRef[MonkeyMonitor] = _
    var parent: ActorRef = _

    def reset(system: ActorSystem, ft: ActorRef): Unit = {
        if (monitor != null) system stop monitor
        if (parent != null) system stop parent
        monitor = TestActorRef.create(system, MonkeyMonitor.props(ft))
        parent = system.actorOf(MonkeyParent.props(monitor))
    }

}
