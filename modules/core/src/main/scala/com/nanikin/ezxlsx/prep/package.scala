package com.nanikin.ezxlsx

package object prep {

  private[ezxlsx] def realRowsLen(_rows: Seq[PrepRow]): Int =
    _rows.size + _rows.map(r => realRowsLen(r.nested)).sum
}
