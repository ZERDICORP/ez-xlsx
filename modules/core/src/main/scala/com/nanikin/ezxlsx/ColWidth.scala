package com.nanikin.ezxlsx

import scala.language.implicitConversions

sealed trait ColWidth

object ColWidth {
  final case class Default(width: Double) extends ColWidth
  final case class CellId(id: String, width: Double) extends ColWidth

  implicit def dblConverter(w: Double): ColWidth =
    Default(w)

  implicit def intConverter(w: Int): ColWidth =
    Default(w)

  implicit def idDblConverter(tpl: (String, Double)): ColWidth =
    CellId(tpl._1, tpl._2)

  implicit def idIntConverter(tpl: (String, Int)): ColWidth =
    CellId(tpl._1, tpl._2)
}
