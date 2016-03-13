package learningconcurrency.ch2

import learningconcurrency._

/**
 * Examples on JVM concurrency model.
 * @author t tomas[dot]duhourq[at]avenida[dot]com
 */
// Deterministic execution
object ThreadMain extends App {
  val t = thread {
    log("Executing on another JVM thread")
  }
  // Halt main thread (puts it in `waiting state`) until t finishes its execution.
  t.join()
  log(s"Thread ${t.getName()} joined.")
}

// As we are joining afterwards, the result of this execution is non-deterministic.
object ThreadUncertain extends App {
  val t = thread {
    log("Executing on another JVM thread")
  }
  log("...")
  log("...")
  t.join()
  log(s"Thread ${t.getName()} joined.")
}
