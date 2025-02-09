package com.nanikin.ezxlsx.prep

import com.nanikin.ezxlsx.Row.Settings

private[ezxlsx] final case class PrepRow(
    id: Option[String],
    cells: Seq[PrepCell],
    nested: Seq[PrepRow],
    settings: Settings
)
