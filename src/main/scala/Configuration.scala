package monkey

import com.typesafe.config.ConfigFactory

/**
  * This object holds the configuration parameters for the system.
  */
object Configuration {

    val conf = ConfigFactory.load()

    val traverseTime = conf.getDuration("monkey.times.on-the-rope")
    val goingTime = conf.getDuration("monkey.times.going")
    val monkeyMinDelay = conf.getDuration("monkey.times.min-delay")
    val monkeyMaxDelay = conf.getDuration("monkey.times.max-delay")

}

