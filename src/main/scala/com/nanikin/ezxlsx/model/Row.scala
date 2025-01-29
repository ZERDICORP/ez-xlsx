package com.nanikin.ezxlsx.model

import cats.implicits.none

private[ezxlsx] sealed trait Row {
  val settings: RowSettings
}

object Row {

  final case class Norm(
      cells: Seq[Cell.Norm],
      nest: Seq[Row] = Seq.empty,
      settings: RowSettings = RowSettings()
  ) extends Row

  private[ezxlsx] trait GenBase extends Row {
    val cells: Seq[Cell]
    val nest: Option[Nest]
  }

  final case class Nest(
      cells: Seq[Cell],
      nest: Option[Row.Nest] = none,
      settings: RowSettings = RowSettings()
  ) extends GenBase

  final case class Gen(
      id: String,
      cells: Seq[Cell],
      nest: Option[Row.Nest] = none,
      settings: RowSettings = RowSettings()
  ) extends GenBase
}
