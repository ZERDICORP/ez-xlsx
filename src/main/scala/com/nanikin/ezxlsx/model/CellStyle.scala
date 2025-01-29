package com.nanikin.ezxlsx.model

sealed trait CellStyle

object CellStyle {
  final case object TextBold extends CellStyle
  final case object TextWrap extends CellStyle
  final case class FontSize(value: Int) extends CellStyle
  final case class TextColorHex(value: String) extends CellStyle
  final case class BgColorHex(value: String) extends CellStyle
  final case class Indent(value: Int) extends CellStyle

  sealed trait Align extends CellStyle

  object Align {
    sealed trait V extends Align

    object V {
      final case object Top extends V
      final case object Bottom extends V
      final case object Center extends V
    }

    sealed trait H extends Align

    object H {
      final case object Left extends H
      final case object Right extends H
      final case object Center extends H
    }
  }
}
