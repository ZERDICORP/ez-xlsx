package com.nanikin.ezxlsx.prep

import com.nanikin.ezxlsx.Class
import com.nanikin.ezxlsx.Col
import com.nanikin.ezxlsx.PosMap

private[ezxlsx] final case class PrepSheet(
    name: String,
    id: Option[String],
    cols: Seq[Col],
    colsInFreeze: Int,
    rowsInFreeze: Int,
    rows: Seq[PrepRow],
    styles: Seq[Class],
    poses: PosMap
)
