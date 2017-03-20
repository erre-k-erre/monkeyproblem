package monkey.test.actor

import akka.actor.{ActorRef, Props}
import monkey.Direction
import monkey.actor.MonkeyParent

class TestMonkeyParent(override val monitor: ActorRef) extends MonkeyParent(monitor) {

    var generatorSeq: Seq[(Int, Direction)] = _

    def generator: (Int, Direction) = {
        generatorSeq match {
            case Nil | null => (-1, null)
            case _ =>
                val result = generatorSeq.head
                generatorSeq = generatorSeq.tail
                result
        }

    }

    def receiveGenerator: Receive = {
        case s: Seq[(Int, Direction)] =>
            generatorSeq = s
            schedule()
    }

    override def receive = super.receive orElse receiveGenerator

}

object TestMonkeyParent {
    def props(monitor: ActorRef) =
        Props(classOf[TestMonkeyParent], monitor)
}
