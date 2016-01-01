package learningconcurrency.ch1

/** Exercises on Ch1: A brief Scala review.
  * Created by tom on 1/1/16.
  */
object Ch1 {

  def compose[A, B, C](g: B => C, f: A => B): A => C = a => g(f(a))

  def fuse[A, B](a: Option[A], b: Option[B]): Option[(A, B)] =
    for {
      elemA <- a
      elemB <- b
    } yield (elemA, elemB)

  def fuse2[A, B](a: Option[A], b: Option[B]): Option[(A, B)] = a.flatMap(x => b.map(y => (x, y)))

  def check[T](xs: Seq[T])(pred: T => Boolean) = xs.foldLeft(true)((acum, elem) => acum && pred(elem))

  def check2[T](xs: Seq[T])(pred: T => Boolean) = xs.forall(pred)

  def permutations(xs: String) = xs.permutations

  def permutations2(s: String): List[String] = {
    def merge(ins: String, c: Char): Seq[String] =
      for (i <- 0 to ins.length) yield
        ins.substring(0, i) + c + ins.substring(i, ins.length)

    if (s.length() == 1)
      s :: Nil
    else
      permutations2(s.substring(0, s.length - 1)).flatMap(p =>
        merge(p, s.charAt(s.length - 1))
      )
  }
}
