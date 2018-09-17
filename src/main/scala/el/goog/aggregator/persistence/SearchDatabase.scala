package el.goog.aggregator.persistence

import akka.http.javadsl.model.headers.LastModified
import com.outworkers.phantom.builder.query.CreateQuery.Default
import com.outworkers.phantom.dsl._
import el.goog.aggregator.persistence.Connector._

import scala.concurrent.Future

class SearchDatabase(override val connector: KeySpaceDef) extends Database[SearchDatabase](connector) {

  object searchModel extends SearchModel with connector.Connector

  def store(search: Search): Future[_root_.com.outworkers.phantom.dsl.ResultSet] = searchModel.store(search)

  def update(search: Search): Future[_root_.com.outworkers.phantom.dsl.ResultSet] = searchModel.updateResult(search)

  def getSearchIdsGt(id: Int): Future[List[Search]] = searchModel.getSearchIdsGt(id)

  def getSearchIdGtLastModifiedGt(id: Int, lastModified: DateTime): Future[List[Search]] = searchModel.getSearchIdsGtModifiedAfter(id, lastModified)

  def autoCreate(): Default[SearchModel, Search] = searchModel.create.ifNotExists()
}

object ProductionDb extends SearchDatabase(connector)

object TestDb extends SearchDatabase(testConnector)
