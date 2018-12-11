package io.coderunner.adventure

import scala.concurrent.{Future, Promise}

// Helper class to translate an event driven callback into a Future that we can wait to be completed
class Channel[T](init: (T => Unit) => Unit, cond: T => Boolean){
  init(update)
  private[this] var value: Promise[T] = null
  def apply(): Future[T] = {
    value = Promise[T]()
    value.future
  }
  def update(t: T): Unit = {
    if (value != null && !value.isCompleted && cond(t)) value.success(t)
  }
}