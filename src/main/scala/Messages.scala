package monkey.message

import monkey.Direction

sealed trait MonitorMessage
case object Go extends MonitorMessage

sealed trait MonkeyMessage
case object OnTheRope extends MonkeyMessage
case object Done extends MonkeyMessage

case class CreateMonkey(dir: Direction)