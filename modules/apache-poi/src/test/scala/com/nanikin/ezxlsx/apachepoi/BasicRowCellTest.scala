//package com.nanikin.ezxlsx.apachepoi
//
//import com.nanikin.ezxlsx._
//import org.scalatest.funsuite.AnyFunSuite
//
//class BasicRowCellTest extends AnyFunSuite {
//
//  private val sheet = Sheet.declare(name = "Test", id = "test")(
//    Data.Gen(
//      id = "row",
//      cells = Seq(Cell.Arg())
//    )
//  )
//
//  private val template = Template
//    .sheet(sheet)
//    .fixate()
//
//  test("Generate 3 rows with 1 ONE arg") {
//    val data = Seq(
//      Arg.Row(
//        args = Seq(Arg.Default("Value1"))
//      ),
//      Arg.Row(
//        args = Seq(Arg.Default("Value2"))
//      ),
//      Arg.Row(
//        args = Seq(Arg.Default("Value3"))
//      )
//    )
//
//    val wb = template
//      .extend()
//      .row("test", "row", data)
//      .interpret()
//      .result
//
//    val sheet = wb.getSheet("Test")
//
//    assert(sheet.getPhysicalNumberOfRows == 3)
//
//    assert(sheet.getRow(0).getCell(0).toString == "Value1")
//    assert(sheet.getRow(1).getCell(0).toString == "Value2")
//    assert(sheet.getRow(2).getCell(0).toString == "Value3")
//  }
//}
