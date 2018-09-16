package el.goog.aggregator.persistence

import com.outworkers.phantom.connectors.{CassandraConnection, ContactPoint, ContactPoints}
import el.goog.aggregator.util.Conf

import scala.collection.JavaConverters._

object Connector {
  private val config = Conf.config

  private val nodes = config.getStringList("cassandra.node")
  private val keyspace = config.getString("cassandra.keyspace")
  private val username = config.getString("cassandra.username")
  private val password = config.getString("cassandra.password")

  /**
    * Create a connector with the ability to connects to
    * multiple hosts in a secured cluster
    */
  lazy val connector: CassandraConnection = ContactPoints(nodes.asScala)
    .withClusterBuilder(_.withCredentials(username, password))
    .keySpace(keyspace)

  /**
    * Create an embedded connector, testing purposes only
    */
  lazy val testConnector: CassandraConnection = ContactPoint.embedded.noHeartbeat().keySpace(keyspace)
}