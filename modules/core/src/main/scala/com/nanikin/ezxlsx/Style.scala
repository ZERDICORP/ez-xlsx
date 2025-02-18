package com.nanikin.ezxlsx

sealed trait Style

object Style {
  sealed trait Conditional extends Style

  object Conditional {

    final case class BgColorHexByNumber(
        start: (Double, String),
        mid: (Double, String),
        end: (Double, String)
    ) extends Conditional
  }

  final case class DataFormat(value: String) extends Style

  final case object TextBold extends Style
  final case object TextWrap extends Style

  final case class FontFamily(value: String) extends Style
  final case class FontSize(value: Int) extends Style
  final case class TextColorHex(value: String) extends Style
  final case class BgColorHex(value: String) extends Style
  final case class Indent(value: Int) extends Style

  sealed trait Align extends Style

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
