package learningconcurrency.ch3

import java.util.concurrent.atomic.AtomicLong
import learningconcurrency._

/**
 * These abstractions permit several memory reads but
 * only one concurrent write.
 *
 * They support linearizable operations, which are operations
 * that seem to occur instantaneously to the rest of the system.
 * An example is a volatile write (to the main memory)
 */
object AtomicPrimitives_2 extends App {

  /**
   * Reimplementation of the getId with atomic mutation.
   * the incrementAndGet method:
   *
   * - Reads the value of uid
   * - Increments it by one
   * - Returns the value
   *
   * in a single operation as if it were a unique transaction
   */
  val uid = new AtomicLong(0L)
  def getUniqueId(): Long = uid.incrementAndGet()
  execute(log(s"Get Unique Id 1: $getUniqueId"))
  execute(log(s"Get Unique Id 2: $getUniqueId"))

  Thread.sleep(200)

  /**
   * These abstractions possess an important method which
   * is the compareAndSet() (CAS operation). This method
   * updates the value provided only if the first parameter
   * is the current value of the variable
   */
  assert(uid.compareAndSet(2, 42))
}
