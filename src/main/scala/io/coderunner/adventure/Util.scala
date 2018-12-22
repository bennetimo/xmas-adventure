package io.coderunner.adventure

object Util {

  def combinedString[A](items: List[A]): String = {

    def loop(items: List[A], built: String): String = items match {
      case Nil => ""
      case r :: Nil => s"${built} and $r"
      case r :: t => loop(t, s"${built} $r, ")
    }
    loop(items, "")
  }
}
