package com.nanikin.ezxlsx.model

private[ezxlsx] final case class Sheet(
    name: String,
    id: Option[String],
    rows: Seq[Row],
    columnWidth: Seq[Int]
)
