package learningconcurrency.ch2

import parallelprogramming._

/**
  * The JVM creates the `main` thread when starting
  * any application. This thread executes the block
  * inside the main method inside this object.
  */
object ThreadsCreation extends App {
  class MyThread extends Thread {
    override def run(): Unit =
      log("New thread running")
  }
  val t = new MyThread
  t.start()
  t.join()
  log("New thread joined.")
}
