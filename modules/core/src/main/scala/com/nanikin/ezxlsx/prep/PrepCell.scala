package com.nanikin.ezxlsx.prep

import com.nanikin.ezxlsx.Cell.Settings
import com.nanikin.ezxlsx.ValueMapper
import com.nanikin.ezxlsx.Value

private[ezxlsx] final case class PrepCell(
    id: Option[String],
    value: Option[Value],
    classes: Seq[String],
    settings: Settings,
    xy: (Int, Int),
    inOneCopy: Boolean,
    mapper: Option[ValueMapper]
)
