package monkey.message

/**
  * The trait <code>Direction</code> is used to indicate the direction of a [[monkey.actor.Monkey]].
  */
sealed trait Direction

/**
  * East to West direction (i.e. going <b>left</b>).
  */
case object EastToWest extends Direction
/**
  * West to East direction (i.e. going <b>right</b>).
  */
case object WestToEast extends Direction
/**
  * Messages sent from the [[monkey.actor.MonkeyMonitor]] to the [[monkey.actor.Monkey]].
  */
sealed trait MonitorMessage
/**
  * This message will be sent from the monitor to the [[monkey.actor.Monkey]] when he is allowed to traverse the rope.
  */
case object Go extends MonitorMessage
/**
  * Messages sent from the [[monkey.actor.Monkey]] to the [[monkey.actor.MonkeyMonitor]].
  */
sealed trait MonkeyMessage
/**
  * This message is sent when the [[monkey.actor.Monkey]] grabs the rope and starts traversing it, i.e. after receiving
  * the [[Go]] message and before the [[Done]] message is produced.
  */
case object OnTheRope extends MonkeyMessage
/**
  * This message is sent when the [[monkey.actor.Monkey]] finish traversing the rope.
  */
case object Done extends MonkeyMessage