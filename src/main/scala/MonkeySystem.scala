import akka.actor.{ActorSystem, Props}
import monkey.actor.{MonkeyMonitor, RandomMonkeyParent}

/**
  * Created by asantuy on 16/03/2017.
  */
object MonkeySystem {
    def apply(system: ActorSystem): Unit = {
        val monitor = system.actorOf(MonkeyMonitor.props)
        system.actorOf(RandomMonkeyParent.props(monitor))
    }

    def apply(): ActorSystem = {
        val system = ActorSystem()
        apply(system)
        system
    }
}
