package monkey.test.system

import akka.actor.{ActorRef, ActorSystem}
import monkey.actor.MonkeyMonitor
import monkey.test.actor.TestMonkeyParent

/**
  * Created by asantuy on 16/03/2017.
  */
object TestMonkeySystem {

    var monitor: ActorRef = _
    var parent: ActorRef = _

    def set(system: ActorSystem): Unit = {
        monitor = system.actorOf(MonkeyMonitor.props)
        parent = system.actorOf(TestMonkeyParent.props(monitor))
    }

    def reset(system: ActorSystem): Unit = {
        system stop monitor
        system stop parent
        set(system)
    }
}
