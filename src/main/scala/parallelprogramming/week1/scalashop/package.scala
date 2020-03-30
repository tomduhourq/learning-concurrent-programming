package parallelprogramming

import java.util.concurrent._

import org.scalameter._
import parallelprogramming.week1.scalashop.BoxBlurKernelInterface

import scala.util.DynamicVariable

package object scalashop extends BoxBlurKernelInterface {

  /** The value of every pixel is represented as a 32 bit integer. */
  type RGBA = Int

  /** The type of a coordinate */
  type Coordinate = (Int, Int)

  /** Returns the red component. */
  def red(c: RGBA): Int = (0xff000000 & c) >>> 24

  /** Returns the green component. */
  def green(c: RGBA): Int = (0x00ff0000 & c) >>> 16

  /** Returns the blue component. */
  def blue(c: RGBA): Int = (0x0000ff00 & c) >>> 8

  /** Returns the alpha component. */
  def alpha(c: RGBA): Int = (0x000000ff & c) >>> 0

  /** Used to create an RGBA value from separate components. */
  def rgba(r: Int, g: Int, b: Int, a: Int): RGBA = {
    (r << 24) | (g << 16) | (b << 8) | (a << 0)
  }

  /** Restricts the integer into the specified range. */
  def clamp(v: Int, min: Int, max: Int): Int = {
    if (v < min) min
    else if (v > max) max
    else v
  }

  /** Image is a two-dimensional matrix of pixel values. */
  class Img(val width: Int, val height: Int, private val data: Array[RGBA]) {
    def this(w: Int, h: Int) = this(w, h, new Array(w * h))
    def apply(x: Int, y: Int): RGBA = data(y * width + x)
    def update(x: Int, y: Int, c: RGBA): Unit = data(y * width + x) = c
  }

  case class Pixel(r: Int, g: Int, b: Int, a: Int) {
    def +(other: Pixel): Pixel = this.copy(
      r = this.r + other.r,
      g = this.g + other.g,
      b = this.b + other.b,
      a = this.a + other.a)

    def /(q: Int) = this.copy(
      r = this.r / q,
      g = this.g / q,
      b = this.b / q,
      a = this.a / q)
  }

  def log(msg: String): Unit = println(s"${Thread.currentThread.getName}: $msg")

  private def calculateRowPixel(img: Img, x: Int, y: Int, yDelta: Int, radius: Int,
                                actualVisitedPixels: Set[Coordinate])
  : (Pixel, Set[Coordinate], Int) = {
    (-radius to radius)
      .foldLeft((Pixel(0, 0, 0, 0), actualVisitedPixels, 0): (Pixel, Set[Coordinate], Int)){
        case ((acumPixel, visited, partialQ), xDelta) =>
          val clampedX = clamp(x + xDelta, 0, img.width - 1)
          val clampedY = clamp(y + yDelta, 0, img.height - 1)
      if (visited contains ((clampedX, clampedY))) {
        // discard
        (acumPixel, visited, partialQ)
      }
      else {
        val pixelRGBA = img.apply(clampedX, clampedY)
        (acumPixel + Pixel(red(pixelRGBA), green(pixelRGBA), blue(pixelRGBA), alpha(pixelRGBA)),
          visited + ((clampedX, clampedY)), partialQ + 1)
      }
    }
  }

  /** Computes the blurred RGBA value of a single pixel of the input image. */
  def boxBlurKernel(src: Img, x: Int, y: Int, radius: Int): RGBA = {
    val visitedPixels = Set[Coordinate]((x, y))
    val (sumPixel, _, quantity) =
      (-radius to radius)
        .foldLeft((Pixel(0, 0, 0, 0), visitedPixels, 0): (Pixel, Set[Coordinate], Int)){
      case ((partialPixelSum, actualVisitedPixels, q), yDelta) =>
        val (rowPixel, newVisitedPixels, pixelsInRow) =
          calculateRowPixel(src, x, y, yDelta, radius, actualVisitedPixels)
        (partialPixelSum + rowPixel, newVisitedPixels, q + pixelsInRow)
    }

    val averagePixel = sumPixel / quantity
    rgba(averagePixel.r, averagePixel.g, averagePixel.b, averagePixel.a)
  }

  val forkJoinPool = new ForkJoinPool

  abstract class TaskScheduler {
    def schedule[T](body: => T): ForkJoinTask[T]
    def parallel[A, B](taskA: => A, taskB: => B): (A, B) = {
      val right = task {
        taskB
      }
      val left = taskA
      (left, right.join())
    }
  }

  class DefaultTaskScheduler extends TaskScheduler {
    def schedule[T](body: => T): ForkJoinTask[T] = {
      val t = new RecursiveTask[T] {
        def compute = body
      }
      Thread.currentThread match {
        case wt: ForkJoinWorkerThread =>
          t.fork()
        case _ =>
          forkJoinPool.execute(t)
      }
      t
    }
  }

  val scheduler =
    new DynamicVariable[TaskScheduler](new DefaultTaskScheduler)

  def task[T](body: => T): ForkJoinTask[T] = {
    scheduler.value.schedule(body)
  }

  def parallel[A, B](taskA: => A, taskB: => B): (A, B) = {
    scheduler.value.parallel(taskA, taskB)
  }

  def parallel[A, B, C, D](taskA: => A, taskB: => B, taskC: => C, taskD: => D): (A, B, C, D) = {
    val ta = task { taskA }
    val tb = task { taskB }
    val tc = task { taskC }
    val td = taskD
    (ta.join(), tb.join(), tc.join(), td)
  }

  // Workaround Dotty's handling of the existential type KeyValue
  implicit def keyValueCoerce[T](kv: (Key[T], T)): KeyValue = {
    kv.asInstanceOf[KeyValue]
  }
}
