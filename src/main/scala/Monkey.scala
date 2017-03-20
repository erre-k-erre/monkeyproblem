package monkey.actor

import java.util.concurrent.TimeUnit

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import monkey.Direction
import monkey.message.{Done, Go, OnTheRope}

import scala.concurrent.duration.Duration

/**
  * Created by asantuy on 16/03/2017.
  */
class Monkey(val direction: Direction, val monitor: ActorRef) extends Actor with ActorLogging {

    import context._

    monitor ! direction

    //log.debug("A new monkey going {}", direction)

    def receive = {
        case Go =>
            system.scheduler.scheduleOnce(Duration.create(1, TimeUnit.SECONDS), self, OnTheRope)
        case OnTheRope =>
            monitor ! OnTheRope
            system.scheduler.scheduleOnce(Duration.create(4, TimeUnit.SECONDS), self, Done)
        case Done =>
            monitor ! Done
            context stop self

    }

}

object Monkey {
    def props(direction: Direction, monitor: ActorRef) = Props(classOf[Monkey], direction, monitor)
}

