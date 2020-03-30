package learningconcurrency.ch2

import parallelprogramming._

/**
  * By calling Thread.sleep(millis), we postpone the execution of the
  * current thread, so the OS can reuse the processor with other
  * threads.
  */
object ThreadsSleep extends App {
  val t = thread {
    Thread.sleep(1000)
    log("New thread running")
    Thread.sleep(1000)
    log("Still running")
    Thread.sleep(1000)
    log("Completed.")
  }
  t.join()
  log("New thread joined.")
}
