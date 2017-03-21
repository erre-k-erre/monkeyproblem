package monkey.actor

import java.util.concurrent.TimeUnit

import scala.util.Random
import akka.actor.{Actor, ActorRef, Props}
import monkey.{Direction, EastToWest, WestToEast}
import monkey.message.CreateMonkey

import scala.concurrent.duration.Duration

/**
  * Created by asantuy on 16/03/2017.
  */
class MonkeyParent(val monitor: ActorRef) extends Actor {

    import context._

    //def generator: (Int, Direction)

    def receive = {
        case CreateMonkey(dir) =>
            actorOf(Monkey.props(dir, monitor))
    }

}

object MonkeyParent {
    def props(monitor: ActorRef) =
        Props(classOf[MonkeyParent], monitor)
}

class RandomMonkeyParent(override val monitor: ActorRef) extends MonkeyParent(monitor) {

    import monkey.Configuration._
    import context._

    schedule()

    def schedule(): Unit = {
        val delay = Random.nextInt((monkeyMaxDelay.toMillis - monkeyMinDelay.toMillis).toInt)
        val dir = if (Random.nextBoolean()) WestToEast else EastToWest

        if (delay >= 0) {
            system.scheduler.scheduleOnce(Duration.create(delay, TimeUnit.MILLISECONDS)) {
                self ! CreateMonkey(dir)
                schedule()
            }
        }

    }
}

object RandomMonkeyParent {
    def props(monitor: ActorRef) = Props(classOf[RandomMonkeyParent], monitor)
}
