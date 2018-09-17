package el.goog.aggregator.search

import akka.actor.{ActorRef, ActorSystem, Props}
import el.goog.aggregator.util.{Conf, Log}

import scala.collection.mutable

/**
  * Entry point for search task execution.
  */
object SearchEngineGate extends Log {
  var refs: mutable.Buffer[ActorRef] = mutable.Buffer.empty

  def loadSearchEngines()(implicit system: ActorSystem): Unit = {
    refs = Conf.providers.flatMap(actorRefForName(_))
  }

  private def actorRefForName(className: String)(implicit system: ActorSystem) = try {
    val actorClass = Class.forName(className)
    Some(system.actorOf(Props(actorClass), actorClass.getSimpleName))
  } catch {
    case _: ClassNotFoundException =>
      log.warn(s"class $className not found. This engine will not be used.")
      None
  }

  def query(query: Query): Unit = {
    refs.foreach(r => r ! query)
  }
}
