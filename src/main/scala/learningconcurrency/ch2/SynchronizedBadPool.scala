package learningconcurrency.ch2

import scala.collection._
import parallelprogramming._

object SynchronizedBadPool extends App {
  private val tasks = mutable.Queue[() => Unit]()

  val worker = new Thread {
    def poll() = tasks.synchronized {
      if(tasks.nonEmpty) Some(tasks.dequeue()) else None
    }

    override def run(): Unit = while (true) poll() match {
      case Some(task) => task()
      case None =>
    }
  }
  worker.setName("worker")
  worker.setDaemon(true)
  worker.start()

  def asynchronous(body: =>Unit) = tasks.synchronized {
    tasks.enqueue(() => body)
  }

  asynchronous { log("Hello ") }
  asynchronous { log("world!") }
  Thread.sleep(5000)
}
