package benchmarks

import scala.collection.mutable.ArrayBuilder
import scala.math.ceil
import scala.concurrent.duration._

object Mark {
  def measure(iterations: Int, unit: TimeUnit = MICROSECONDS)(thunk: => Any): Array[Double] = {
    val times = new ArrayBuilder.ofDouble

    System.gc()
    Thread.sleep(1000)
    // warm up.
    val warmup = ceil(iterations / 10).toInt
    for (_ <- (1 to warmup)) {
      thunk
    }

    // run.
    for (_ <- (1 to iterations)) {
      times += timeFor(thunk).toUnit(unit)
    }
    times.result
  }

  def timeFor(thunk: => Any): FiniteDuration = {
    val before = System.nanoTime
    thunk
    FiniteDuration(System.nanoTime - before, NANOSECONDS)
  }
}
