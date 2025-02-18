package com.nanikin.ezxlsx.apachepoi

import com.nanikin.ezxlsx._
import org.scalatest.funsuite.AnyFunSuite

class SkipNestedFormulaTest extends AnyFunSuite {

  private val sheet = Sheet(
    Row(
      Cell.arg,
      Cell.arg.withId("age"),
      Cell("%s * 2" << "age")
    )
      .withId("row")
      .withNested(
        Row.nested(
          Cell.arg,
          Cell.arg.withId("age-nested"),
          Cell("%s * 2" << "age-nested")
        )
      ),
    Row(
      Cell("Total"),
      Cell("SUM(%s)" << "age"),
      Cell.empty
    )
  ).withId("sheet")

  private val template = Template
    .sheet(sheet)
    .fixate()

  test("sum formula is correct") {
    val data = Seq(
      Data("Alex", 10).withNested(
        Data("Bob", 5),
        Data("Bob2", 5)
      ),
      Data("Marry", 18).withNested(
        Data("Michael", 10),
        Data("Masha", 8)
      ),
      Data("Josh", 5),
      Data("Carol", 30).withNested(
        Data("Marty", 22),
        Data("Marty2", 8)
      )
    )

    val wb = template
      .extend()
      .row("sheet", "row", data)
      .interpret()
      .result

    val sheet = wb.getSheetAt(0)
    val row = sheet.getRow(10)
    val cell = row.getCell(1)

    assert(
      cell.getCellFormula == "SUM(B1, B4, B7:B8)"
    )
  }
}
