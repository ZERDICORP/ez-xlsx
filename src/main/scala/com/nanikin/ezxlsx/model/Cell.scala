package com.nanikin.ezxlsx.model

private[ezxlsx] sealed trait Cell {
  val settings: CellSettings
}

object Cell {

  final case class Norm(
      value: Any,
      styles: Seq[CellStyle] = Seq.empty,
      settings: CellSettings = CellSettings()
  ) extends Cell

  final case class Gen(
      styles: Any => Seq[CellStyle] = _ => Seq.empty,
      settings: CellSettings = CellSettings()
  ) extends Cell
}
