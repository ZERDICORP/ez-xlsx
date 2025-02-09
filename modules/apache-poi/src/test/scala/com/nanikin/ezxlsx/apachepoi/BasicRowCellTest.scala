package com.nanikin.ezxlsx.apachepoi

import com.nanikin.ezxlsx._
import org.scalatest.funsuite.AnyFunSuite

class BasicRowCellTest extends AnyFunSuite {

  private val sheet = Sheet(
    Row(Cell.arg).withId("row")
  )
    .withName("Test")
    .withId("test")

  private val template = Template
    .sheet(sheet)
    .fixate()

  test("Generate 3 rows with 1 ONE arg") {
    val data = Seq(
      Data("Value1"),
      Data(1),
      Data("Value3")
    )

    val wb = template
      .extend()
      .row("test", "row", data)
      .interpret()
      .result

    val sheet = wb.getSheet("Test")

    assert(sheet.getPhysicalNumberOfRows == 3)

    assert(sheet.getRow(0).getCell(0).toString == "Value1")
    assert(sheet.getRow(1).getCell(0).toString == "1.0")
    assert(sheet.getRow(2).getCell(0).toString == "Value3")
  }
}
