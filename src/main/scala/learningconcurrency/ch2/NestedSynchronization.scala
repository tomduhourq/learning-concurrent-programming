package learningconcurrency.ch2

import parallelprogramming._

/**
  * Creates accounts and performs transactions between them.
  *
  * The first example may result in a deadlock when both the accounts
  * try to acquire each one's monitor.
  *
  *
  */
object NestedSynchronization extends App {

  def startThread(account1: Account, account2: Account, times: Int) =
    thread {
      for (_ <- 1 to times) account1.transfer(account2, 1000)
    }

  val a1 = new Account(150000)
  val a2 = new Account(150000)

  println("Expecting accounts a1 and a2 to have the same amount (and to terminate if possible)")
  val t = startThread(a1, a2, 10)
  val s = startThread(a2, a1, 10)

  t.join()
  s.join()

  println(a1.actualAmount)
  println(a2.actualAmount)

  println("Avoid the deadlock with proper access order")
  val a3 = new IdentifiableAccount(150000)
  val a4 = new IdentifiableAccount(150000)

  val tr1 = startThread(a3, a4, 100)
  val tr2 = startThread(a4, a3, 100)

  tr1.join()
  tr2.join()

  println(a3.actualAmount)
  println(a4.actualAmount)
}

class Account(protected var amount: Double) {
  def actualAmount = amount
  def transfer(target: Account, toTransfer: Double) =
    this.synchronized {
      target.synchronized {
        this.amount -= toTransfer
        target.amount += toTransfer
      }
    }
}

final class IdentifiableAccount(private val initialAmount: Double) extends Account(amount = initialAmount) {
  val uid = Atomic.getUid

  private def lockAndTransfer(target: Account, toTransfer: Double) =
    super.transfer(target, toTransfer)

  def transfer(target: IdentifiableAccount, toTransfer: Double) =
    if(this.uid < target.uid) lockAndTransfer(target, toTransfer)
    else target.lockAndTransfer(this, -toTransfer)
}
