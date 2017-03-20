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
abstract class MonkeyParent(val monitor: ActorRef) extends Actor {

    import context._

    def generator: (Int, Direction)

    schedule()

    def receive = {
        case CreateMonkey(dir) =>
            context.actorOf(Monkey.props(dir, monitor))
            schedule()
    }

    protected def schedule() = {
        val (delay, dir) = generator
        if (delay >= 0) {
            system.scheduler.scheduleOnce(Duration.create(delay, TimeUnit.MILLISECONDS), self, CreateMonkey(dir))
        }
    }
}


class RandomMonkeyParent(override val monitor: ActorRef) extends MonkeyParent(monitor) {
    var i = 0

    def generator = {
        //val dir = Random.nextBoolean()
        //(Random.nextInt(7001) + 1000, if (dir) WestToEast else EastToWest)
        i = i + 1
        (3000, if (i % 2 == 0) WestToEast else EastToWest)
    }
}

object RandomMonkeyParent {
    def props(monitor: ActorRef) = Props(classOf[RandomMonkeyParent], monitor)
}
