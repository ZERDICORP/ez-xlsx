package com.nanikin.ezxlsx

import cats.implicits.catsSyntaxOptionId

sealed trait Sheet {
  val name: String
  val id: Option[String]
  val colsWidth: Map[Int, Int]
  val rows: Seq[Row]
  val styles: Seq[Class]
}

object Sheet {

  private[ezxlsx] final case class Default(
      rows: Seq[Row.Default],
      name: String = "unnamed",
      id: Option[String] = None,
      colsWidth: Map[Int, Int] = Map.empty,
      styles: Seq[Class] = Seq.empty
  ) extends Sheet

  def apply(rows: Row.Default*): Default = Default(rows)

  implicit class SheetOps(sheet: Default) {
    def withName(name: String): Default = sheet.copy(name = name)
    def withId(id: String): Default = sheet.copy(id = id.some)
    def withColsWidth(colsWidth: (Int, Int)*): Default = sheet.copy(colsWidth = colsWidth.toMap)
    def withStyles(styles: Seq[Class]): Default = sheet.copy(styles = styles)
  }
}
