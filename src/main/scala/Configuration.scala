package monkey

import com.typesafe.config.ConfigFactory
/**
  * This object holds the configuration parameters for the system.
  */
object Configuration {

    val conf = ConfigFactory.load()
    /**
      * The interval of time that a monkey will spent traversing the rope.
      */
    val traverseTime = conf.getDuration("monkey.times.on-the-rope")
    /**
      * The interval of time that a monkey will spent grabbing the rope.
      */
    val goingTime = conf.getDuration("monkey.times.going")
    /**
      * Monkeys are generated at random intervals. This parameters sets the minimum delay between
      * the creation of two monkeys.
      */
    val monkeyMinDelay = conf.getDuration("monkey.times.min-delay")
    /**
      * This parameters sets the maximun delay between the creation of two monkeys.
      */
    val monkeyMaxDelay = conf.getDuration("monkey.times.max-delay")

}

