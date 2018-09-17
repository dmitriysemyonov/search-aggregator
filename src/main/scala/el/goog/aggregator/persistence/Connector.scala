package el.goog.aggregator.persistence

import com.outworkers.phantom.connectors.{CassandraConnection, ContactPoint, ContactPoints}
import el.goog.aggregator.util.Conf

import scala.collection.JavaConverters._

/**
  * Database connection wrapper.
  */
object Connector {

  /**
    * Create a connector with the ability to connects to
    * multiple hosts in a secured cluster
    */
  lazy val connector: CassandraConnection = ContactPoints(Conf.nodes.asScala)
    .withClusterBuilder(_.withCredentials(Conf.username, Conf.password))
    .keySpace(Conf.keyspace)

  /**
    * Create an embedded connector, testing purposes only
    */
  lazy val testConnector: CassandraConnection = ContactPoint.embedded.noHeartbeat().keySpace(Conf.keyspace)
}