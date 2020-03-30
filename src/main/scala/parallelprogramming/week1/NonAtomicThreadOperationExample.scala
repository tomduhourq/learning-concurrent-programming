package parallelprogramming.week1

import parallelprogramming.thread

/** Access to shared state from two threads in a non atomic way */
object NonAtomicThreadOperationExample extends App {

  private var uid = 0L
  def getUniqueId: Long = {
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
