package el.goog.aggregator.persistence

import com.outworkers.phantom.dsl.{ConsistencyLevel, _}
import org.joda.time.DateTime

import scala.concurrent.Future

abstract class SearchModel extends Table[SearchModel, Search] {
  override def tableName: String = "search"

  object id extends IntColumn with PartitionKey

  object term extends StringColumn

  object priority extends IntColumn

  object num extends IntColumn

  object lastModified extends DateTimeColumn

  object result extends SetColumn[String]

  override def fromRow(r: Row): Search = Search(id(r), term(r), priority(r), num(r), lastModified(r), result(r))

  def getSearchById(id: Int): Future[Option[Search]] = {
    select
      .where(_.id eqs id)
      .consistencyLevel_=(ConsistencyLevel.ONE)
      .one()
  }

  def getSearchIdsGt(id: Int): Future[List[Search]] = {
    select
      .where(_.id gte id)
      .consistencyLevel_=(ConsistencyLevel.ONE)
      .allowFiltering()
      .fetch()
  }

  def getSearchIdsGtModifiedAfter(id: Int, timestamp: DateTime): Future[List[Search]] = {
    select
      .where(_.id gte id)
      .and(_.lastModified isGte timestamp)
      .consistencyLevel_=(ConsistencyLevel.ONE)
      .allowFiltering()
      .fetch()
  }

  def store(search: Search): Future[ResultSet] = {
    insert
      .value(_.id, search.id)
      .value(_.term, search.term)
      .value(_.priority, search.priority)
      .value(_.num, search.num)
      .value(_.lastModified, search.lastModified)
      .consistencyLevel_=(ConsistencyLevel.ONE)
      .future()
  }

  def updateResult(search: Search): Future[ResultSet] = {
    update
      .where(_.id eqs search.id)
      .modify(_.lastModified setTo DateTime.now)
      .and(_.result setTo search.result)
      .future()
  }
}