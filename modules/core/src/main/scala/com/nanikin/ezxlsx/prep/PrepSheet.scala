package com.nanikin.ezxlsx.prep

import com.nanikin.ezxlsx.Class
import com.nanikin.ezxlsx.ColWidth
import com.nanikin.ezxlsx.PosMap

private[ezxlsx] final case class PrepSheet(
    name: String,
    id: Option[String],
    colsWidth: Seq[ColWidth],
    colsInFreeze: Int,
    rowsInFreeze: Int,
    rows: Seq[PrepRow],
    styles: Seq[Class],
    poses: PosMap
)
