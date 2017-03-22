package monkey.actor

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import monkey.Configuration
import monkey.message.{Direction, Done, Go, OnTheRope}

import scala.concurrent.duration.Duration._
/**
  * The monkey class represents the independent process indicated in the requirements.
  * Upon creation, the [[Monkey]] sends a message to the [[MonkeyMonitor]], so it could be enqueued.
  * The monkey starts processing when he receives the [[Go]] message from the monitor.
  * Internally, it holds a scheduler that triggers the sending of the different messages ([[OnTheRope]] and [[Done]])
  * to the monitor.
  * @constructor Creates a new monkey.
  * @param direction The direction faced by the monkey.
  * @param monitor The [[akka.actor.ActorRef]] to the actor acting as monitor in the system.
  */
class Monkey(val direction: Direction, val monitor: ActorRef) extends Actor with ActorLogging {

    val scheduler = context.system.scheduler

    log.debug("New Monkey with direction {}!!", direction)
    // The monitor is informed of the arrival of a new monkey by a given direction.
    monitor ! direction

    def receive: Receive = {
        case Go =>
            // The first step is to grab the rope, as the instructions state. After some given interval
            // the monkey grabs the rope and starts traversing it.
            scheduler.scheduleOnce(fromNanos(Configuration.goingTime.toNanos), self, OnTheRope)
        case OnTheRope =>
            // Message is forwarded to the monitor.
            monitor ! OnTheRope
            // When the monkeys traverses the rope, the Done message is sent to the monitor.
            scheduler.scheduleOnce(fromNanos(Configuration.traverseTime.toNanos), self, Done)
        case Done =>
            // Message is forwarded to the monitor.
            monitor ! Done
            // Monkey is stopped as his job is done.
            context stop self

    }

}

object Monkey {
    def props(direction: Direction, monitor: ActorRef) = Props(classOf[Monkey], direction, monitor)
}

