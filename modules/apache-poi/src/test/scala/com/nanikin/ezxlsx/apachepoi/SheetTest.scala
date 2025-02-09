package com.nanikin.ezxlsx.apachepoi

import com.nanikin.ezxlsx._
import org.scalatest.funsuite.AnyFunSuite

class SheetTest extends AnyFunSuite {

  private val sheet1 = Sheet().withName("Sheet1")
  private val sheet2 = Sheet().withName("Sheet2")
  private val sheet3 = Sheet().withName("Sheet3")

  private val template = Template
    .sheet(sheet1)
    .sheet(sheet2)
    .sheet(sheet3)
    .fixate()

  test("Workbook has 3 sheets with specified names") {
    val wb = template
      .extend()
      .interpret()
      .result

    assert(wb.getNumberOfSheets == 3)

    val expectedSheets = Seq("Sheet1", "Sheet2", "Sheet3")
    val actualSheets = (0 until wb.getNumberOfSheets).map(wb.getSheetName)

    assert(expectedSheets == actualSheets)
  }
}
