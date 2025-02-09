package com.nanikin.ezxlsx

import com.nanikin.ezxlsx.prep.PrepSheet
import org.scalatest.funsuite.AnyFunSuite

class ExtenderSimpleRowTest extends AnyFunSuite {

  private val sheet = Sheet(
    Row(
      Cell("Cell-1"),
      Cell("Cell-2")
    )
  )

  private val template = Template
    .sheet(sheet)
    .fixate()

  test("Sheet contains 1 row with 2 filled cells") {
    val sheets: Seq[PrepSheet] = template
      .extend()
      .prepare()

    assert(sheets.head.rows.size == 1)

    val row = sheets.head.rows.head
    assert(row.cells.size == 2)
    assert(row.cells.count(_.inOneCopy) == 2)
    assert {
      row.cells.flatMap(_.value) === Seq("Cell-1", "Cell-2").map(Value.convertToCellValue[String])
    }
  }
}
