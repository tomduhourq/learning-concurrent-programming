package parallelprogramming.week1

import parallelprogramming.thread

/** Example program that incurs in a deadlock example */
object DeadlockAccountsExample extends App {

  case class Account(private var balance: Int = 0) {
    def transfer(account: Account, amount: Int): Unit =
      this.synchronized {
      account.synchronized {
        this.balance -= amount
        account.balance += amount
      }
    }
  }

  val a1 = Account(100)
  val a2 = Account(100)

  def block(from: Account, to: Account, amount: Int): Unit = {
    for (_ <- 0 until amount) from.transfer(to, amount = 1)
  }

  val t = thread(block(a1, a2, 100))
  val s = thread(block(a2, a1, 100))

  t.join()
  s.join()

}
