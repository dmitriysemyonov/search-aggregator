package el.goog.aggregator.util

import com.typesafe.config.{Config, ConfigFactory}

import scala.collection.JavaConverters._
import scala.collection.mutable

/**
  * Application configuration holder.
  */
object Conf {
  val config: Config = ConfigFactory.load()

  //container
  val port: Int = config.getInt("app.port")


  //database
  val nodes = config.getStringList("cassandra.node")
  val keyspace = config.getString("cassandra.keyspace")
  val username = config.getString("cassandra.username")
  val password = config.getString("cassandra.password")


  //search providers
  val providers: mutable.Buffer[String] = config.getStringList("search.provider").asScala

}
