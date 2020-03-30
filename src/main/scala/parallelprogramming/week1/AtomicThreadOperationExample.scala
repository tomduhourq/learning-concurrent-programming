package parallelprogramming.week1

import parallelprogramming.thread

/** Access to the shared state is synchronized. The sequence of ids is respected */
object AtomicThreadOperationExample extends App {

  private var uid = 0L
  def getUniqueId: Long = synchronized {
    uid = uid + 1
    uid
  }

  def block(): Unit = {
    val uids = for (_ <- 1 to 10) yield getUniqueId
    println(uids)
  }

  val t = thread(block())
  val s = thread(block())

  t.join()
  s.join()
}
