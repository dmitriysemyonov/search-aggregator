package el.goog.aggregator.util

import com.typesafe.config.{Config, ConfigFactory}

object Conf {
  val config: Config = ConfigFactory.load()
  val port: Int = config.getInt("app.port")
}
