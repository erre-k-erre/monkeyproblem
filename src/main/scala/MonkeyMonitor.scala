package monkey.actor

import java.util.concurrent.TimeUnit

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import monkey.message.{Done, Go, OnTheRope}
import monkey.{Direction, EastToWest, WestToEast}

import scala.concurrent.duration.Duration


/**
  * Created by asantuy on 16/03/2017.
  */
class MonkeyMonitor extends Actor with ActorLogging {

    import context._

    var currentDirection: Direction = _
    var fromWest = List.empty[ActorRef]
    var fromEast = List.empty[ActorRef]
    var passing = Set.empty[ActorRef]
    var going = false

    system.scheduler.schedule(
        Duration.create(0, TimeUnit.SECONDS),
        Duration.create(1, TimeUnit.SECONDS),
        self, LogState)

    def receive = {

        case d: Direction =>
            d match {
                  case WestToEast =>
                      fromWest = fromWest :+ sender()
                  case EastToWest =>
                      fromEast = fromEast :+ sender()
            }
            if (passing.isEmpty) {
                sendGo(d)
            } else if (d == currentDirection && !going && oppositeQueue.isEmpty) {
                sendGo(d)
            }

        case OnTheRope =>
            going = false
            if(oppositeQueue.isEmpty) {
                sendGo(currentDirection)
            }

        case Done =>
            passing = passing - sender()

            if (passing.isEmpty) {
                currentDirection match {
                    case EastToWest => sendGo(WestToEast)
                    case WestToEast => sendGo(EastToWest)
                }
            }

        case QueryPassingMonkeys => sender() ! passing
        case QueryWestQueueMonkeys => sender() ! fromWest
        case QueryEastQueueMonkeys => sender() ! fromEast

        case LogState =>
            logState()

    }

    private def oppositeQueue = {
        currentDirection match {
            case WestToEast => fromEast
            case EastToWest => fromWest
        }
    }
    private def sendGo(direction: Direction) = {

        def newState(queue: List[ActorRef]) = {
            if (!queue.isEmpty) {
                going = true
                val monkey = queue.head
                passing = passing + monkey
                currentDirection = direction
                monkey ! Go
                queue.tail
            } else {
                queue
            }
        }

        direction match {
            case WestToEast => fromWest = newState(fromWest)
            case EastToWest => fromEast = newState(fromEast)
        }
    }

    private def logState() = {
        log.debug("{} --------------------- {} {} --------------------- {}",
            fromWest.size,
            if (currentDirection == WestToEast) passing.size else "<=",
            if (currentDirection == WestToEast) "=>" else  passing.size,
            fromEast.size)
    }
}

private case object LogState
case object QueryWestQueueMonkeys
case object QueryEastQueueMonkeys
case object QueryPassingMonkeys

object MonkeyMonitor {
    val props = Props(classOf[MonkeyMonitor])
}