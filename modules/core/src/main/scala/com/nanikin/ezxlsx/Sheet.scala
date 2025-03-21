package com.nanikin.ezxlsx

import cats.implicits.catsSyntaxOptionId

sealed trait Sheet {
  val name: String
  val id: Option[String]
  val cols: Seq[Col]
  val colsInFreeze: Int
  val rowsInFreeze: Int
  val rows: Seq[Row]
  val styles: Seq[Class]
}

object Sheet {

  private[ezxlsx] final case class Default(
      rows: Seq[Row.Default],
      name: String = "unnamed",
      id: Option[String] = None,
      cols: Seq[Col] = Seq.empty,
      colsInFreeze: Int = 0,
      rowsInFreeze: Int = 0,
      styles: Seq[Class] = Seq.empty
  ) extends Sheet

  def apply(rows: Row.Default*): Default = Default(rows)

  implicit class SheetOps(sheet: Default) {
    def withName(name: String): Default = sheet.copy(name = name)
    def withId(id: String): Default = sheet.copy(id = id.some)
    def withCols(cols: Col*): Default = sheet.copy(cols = cols)

    def withFreeze(cols: Int = 0, rows: Int = 0): Default = sheet.copy(
      colsInFreeze = cols,
      rowsInFreeze = rows
    )
    def withStyles(styles: Seq[Class]): Default = sheet.copy(styles = styles)
  }
}
