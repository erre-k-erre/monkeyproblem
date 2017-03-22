import akka.actor.ActorSystem
import monkey.actor.{MonkeyMonitor, RandomMonkeyParent}

import scala.io.StdIn

/**
  * [[App]] object to start the system.
  */
object MonkeyApp extends App {
    val system = MonkeySystem()
    system.log.info("Press ENTER to stop the system....")
    StdIn.readLine()
    system.terminate()
}

/**
  * This object can be used to generate a system of monkeys and ropes :)
  */
object MonkeySystem {
    /**
      * Creates the needed actors ([[MonkeyMonitor]] and a [[RandomMonkeyParent]]) in the given system
      * and starts to produce monkeys.
      * @param system An [[ActorSystem]].
      */
    def apply(system: ActorSystem): Unit = {
        val monitor = system.actorOf(MonkeyMonitor.props)
        system.actorOf(RandomMonkeyParent.props(monitor))
    }

    /**
      * Return a new Actor System named <i>Monkey System</i> with the actors [[MonkeyMonitor]]
      * and a [[RandomMonkeyParent]].
      * It immediately starts to produce monkeys.
      * @return An [[ActorSystem]].
      */
    def apply(): ActorSystem = {
        val system = ActorSystem("Monkey System")
        apply(system)
        system
    }
}