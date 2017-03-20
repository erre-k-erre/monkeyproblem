package monkey
/**
  * Created by asantuy on 16/03/2017.
  */
sealed trait Direction

case object EastToWest extends Direction
case object WestToEast extends Direction

