package com.nanikin.ezxlsx.prep

import com.nanikin.ezxlsx.PosMap
import com.nanikin.ezxlsx.Class

private[ezxlsx] final case class PrepSheet(
    name: String,
    id: Option[String],
    colsWidth: Map[Int, Int],
    colsInFreeze: Int,
    rowsInFreeze: Int,
    rows: Seq[PrepRow],
    styles: Seq[Class],
    poses: PosMap
)
