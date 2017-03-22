package monkey.actor

import java.util.concurrent.TimeUnit

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import monkey.message._
import scala.concurrent.duration.Duration
/**
  * This actor controls the access to the rope.
  * It is implemented using to queues (one for [[Monkey]]s coming from the West, and the other for the [[Monkey]]s
  * coming from the East), and a [[Set]] holding the [[Monkey]]s that are currently traversing the rope.
  * @constructor Creates a new [[MonkeyMonitor]].
  * @param ft All messages processed by the monitor will be forwarded to this [[Actor]]. Useful for testing purposes.
  */
class MonkeyMonitor(ft: ActorRef = Actor.noSender) extends Actor with ActorLogging {
    import context._
    // The current direction of the Monkeys that are traversing the rope.
    var currentDirection: Direction = _
    // The queue for the monkeys coming from the West.
    var fromWest: List[ActorRef] = List.empty[ActorRef]
    // The queue for the monkeys coming from the East.
    var fromEast: List[ActorRef] = List.empty[ActorRef]
    // The set that hold the monkeys that are currently passing the canyon.
    var passing: Set[ActorRef] = Set.empty
    // This flag indicates if some monkey is still grabbing the rope, as indicated by the instructions.
    var going: Boolean = false
    // Each second, a snapshot of the system will be printed, in order to ease the system debugging and monitoring.
    private case object LogState
    context.system.scheduler.schedule(
        Duration.create(0, TimeUnit.SECONDS),
        Duration.create(1, TimeUnit.SECONDS),
        self, LogState)

    def receive: Receive = {
        // A new monkey arrives from 'd'.
        case d: Direction =>
            // The monkey is enqueued.
            d match {
                  case WestToEast =>
                      fromWest = fromWest :+ sender()
                  case EastToWest =>
                      fromEast = fromEast :+ sender()
            }
            // If the rope is empty, he is allowed to go.
            if (passing.isEmpty) {
                sendGo(d)
            // Otherwise, the monkey can pass only if a series of conditions holds.
            //   1. Monkeys are passing in his direction.
            //   2. No monkey is trying to grab the rope.
            //   3. The opposite queue must be empty.
            } else if (d == currentDirection && !going && oppositeQueue.isEmpty) {
                sendGo(d)
            }
            forwardMessage(d)
        // A monkey has grab the rope and is traversing the canyon.
        case msg @ OnTheRope =>
            // The flag is switched, so another monkey facing the same direction can pass.
            going = false
            // If the opposite queue is empty a new monkey facing the same direction can try to pass.
            if(oppositeQueue.isEmpty) {
                sendGo(currentDirection)
            }
            forwardMessage(msg)

        case msg @ Done =>
            // The passing Set is updated.
            passing = passing - sender()
            // If no monkey is passing, is time to monkeys from the other side to pass.
            if (passing.isEmpty) {
                currentDirection match {
                    case EastToWest => sendGo(WestToEast)
                    case WestToEast => sendGo(EastToWest)
                }
            }
            forwardMessage(msg)

        case LogState =>
            logState()

    }

    /*
     * Forward a message to the ft Actor.
     */
    private def forwardMessage(msg: Any) = {
        if (ft != null) ft forward msg
    }
    /*
     * Convenience method to retrieve the queue of monkeys that are not passing at the current moment.
     */
    private def oppositeQueue = {
        currentDirection match {
            case WestToEast => fromEast
            case EastToWest => fromWest
        }
    }
    /*
     * This method will send the Go message to the given queue. Collections are updated consistently.
     */
    private def sendGo(direction: Direction) = {

        def newState(queue: List[ActorRef]) = {
            // If the target queue is empty, there's nothing to deal with.
            if (queue.nonEmpty) {
                // The first monkey is retrieved.
                val monkey = queue.head
                // The actor state is updated.
                going = true
                passing = passing + monkey
                currentDirection = direction
                // The monkey can proceed.
                monkey ! Go
                // The new state is the same queue minus the first element.
                queue.tail
            } else {
                // If the queue was empty, there is no need to update it.
                queue
            }
        }

        // The queue state is updated.
        direction match {
            case WestToEast => fromWest = newState(fromWest)
            case EastToWest => fromEast = newState(fromEast)
        }
    }
    /*
     * A graphical representation of the state of the actor.
     */
    private def logState() = {
        log.debug("{} --------------------- {} {} --------------------- {}",
            fromWest.size + (if(currentDirection == WestToEast && going) " *" else "  "),
            if (currentDirection == WestToEast) passing.size else "<=",
            if (currentDirection == WestToEast) "=>" else  passing.size,
            (if(currentDirection == EastToWest && going) "* " else "  ") + fromEast.size)
    }
}

object MonkeyMonitor {

    val props = Props(classOf[MonkeyMonitor], Actor.noSender)
    def props(ft: ActorRef) = Props(classOf[MonkeyMonitor], ft)

}