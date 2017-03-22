package monkey.actor

import java.util.concurrent.TimeUnit

import scala.util.Random._
import akka.actor.{Actor, ActorRef, Props}
import monkey.message.{Direction, EastToWest, WestToEast}

import scala.concurrent.duration.Duration

/**
  * This actor will be the supervisor of all [[Monkey]] instances in the system. To create a new [[Monkey]]
  * it is necessary that this actor receives a [[Direction]] message. The monkey is then responsible to inform
  * the [[MonkeyMonitor]] that is ready to traverse the rope when indicated.
  * @constructor Creates a new parent.
  * @param monitor The reference to a [[MonkeyMonitor]] that will be passed to a [[Monkey]] when it is created.
  */
class MonkeyParent(val monitor: ActorRef) extends Actor {

    def receive: Receive = {
        case dir: Direction => context.actorOf(Monkey.props(dir, monitor))
    }

}

object MonkeyParent {
    def props(monitor: ActorRef) =
        Props(classOf[MonkeyParent], monitor)
}

/**
  * This subclass of [[MonkeyParent]] schedules creation messages ([[Direction]]) to itself at random intervals
  * (see [[monkey.Configuration]]). [[Direction]] is also chosen randomly.
  * @param monitor The reference to a [[MonkeyMonitor]] that will be passed to a [[Monkey]] when it is created.
  */
class RandomMonkeyParent(override val monitor: ActorRef) extends MonkeyParent(monitor) {

    import monkey.Configuration._
    import context._

    // Upon creation the first Monkey instantiation is scheduled.
    schedule()

    def schedule(): Unit = {
        // We generate the random variables that will be used by the scheduler.
        // Notice that the interval is expressed in millis an casted to Int. This may cause overflow problems
        // if the interval is too big, but is necessary in order to use the nextInt function (no similar nextLong(Long)
        // function is available in the Random API).
        val delay = nextInt((monkeyMaxDelay.toMillis - monkeyMinDelay.toMillis).toInt) + monkeyMinDelay.toMillis.toInt
        val dir = if (nextBoolean()) WestToEast else EastToWest

        system.scheduler.scheduleOnce(Duration.create(delay, TimeUnit.MILLISECONDS)) {
            // This message will be processed by the super-class (MonkeyParent) receive method.
            self ! dir
            // The process schedules itself infinitely.
            schedule()
        }

    }
}

object RandomMonkeyParent {
    def props(monitor: ActorRef) = Props(classOf[RandomMonkeyParent], monitor)
}
