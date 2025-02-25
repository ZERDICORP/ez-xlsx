package com.nanikin.ezxlsx

import cats.implicits.catsSyntaxOptionId

sealed trait Col

object Col {
  val DEFAULT_WIDTH = 8.43

  private[ezxlsx] final case class Default(
      id: Option[String] = None,
      width: Double = DEFAULT_WIDTH,
      collapse: Boolean = false,
      repeats: Int = 1
  ) extends Col

  def apply(width: Double = DEFAULT_WIDTH, collapse: Boolean = false): Default =
    Default(width = width, collapse = collapse)

  implicit class ops(col: Default) {
    def withId(id: String): Default = col.copy(id = id.some)
    def repeat(times: Int): Default = col.copy(repeats = times)
  }
}
