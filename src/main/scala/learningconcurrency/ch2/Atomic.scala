package learningconcurrency.ch2

import learningconcurrency._

/**
  * Two side effect executions with and without synchronization
  */
object Atomic extends App {

  private var counter = 0L

  println("No atomicity!")
  def upCount: Long = {
    counter = counter + 1L
    counter
  }

  val t1 = thread {
    for(_ <- 1 to 10) log(upCount.toString)
  }

  val t2 = thread {
    for(_ <- 1 to 10) log(upCount.toString)
  }

  t1.join()
  t2.join()

  println("Do it with atomicity!")
  counter = 0L

  def upCountSync = this.synchronized{
    upCount
    log(counter.toString)
  }

  val atomic1 = thread {
    for(_ <- 1 to 10) upCountSync
  }

  val atomic2 = thread {
    for(_ <- 1 to 10) upCountSync
  }

  atomic1.join()
  atomic2.join()
}
