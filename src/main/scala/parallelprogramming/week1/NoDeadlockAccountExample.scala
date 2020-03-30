package parallelprogramming.week1

import parallelprogramming.thread

/** Solution to the deadlock problem: execute the transfer block based on ordering ids */
object NoDeadlockAccountExample extends App {

  case class Account(private var balance: Int = 0, id: Int) {

    def transfer(account: Account, amount: Int): Unit =
      if (this.id < account.id) {
        lockAndTransfer(account, amount)
      }
      else {
        // target account transfers to me
        lockAndTransfer(account, -amount)
      }

    private def lockAndTransfer(account: Account, amount: Int): Unit = {
      this.synchronized {
        account.synchronized {
          this.balance -= amount
          account.balance += amount
        }
      }
    }
  }

  val a1 = Account(100, id = 0)
  val a2 = Account(100, id = 1)

  def block(from: Account, to: Account, amount: Int): Unit = {
    for (_ <- 0 until amount) from.transfer(to, amount = 1)
  }

  val t = thread(block(a1, a2, 50))
  val s = thread(block(a2, a1, 100))

  t.join()
  s.join()
}
