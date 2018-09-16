package el.goog.aggregator.search

class DummySearchEngine2 extends DummySearchEngine {
  override val offset = 1

  override def find(term: String, num: Int): List[String] = {
    Thread.sleep(10000)
    super.find(term, num)
  }
}
