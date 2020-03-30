package parallelprogramming.week1

import parallelprogramming.thread

/** Asynchronism in the message */
object ThreadExample extends App {


  def block(): Unit = {
    println("Hello")
    println("World!")
  }

  val t = thread(block())
  val s = thread(block())

  t.join()
  s.join()
}
