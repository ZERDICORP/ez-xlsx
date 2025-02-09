package com.nanikin.ezxlsx

import com.nanikin.ezxlsx.prep.PrepSheet
import org.scalatest.funsuite.AnyFunSuite

class ExtenderRowTest extends AnyFunSuite {

  private val sheet = Sheet(
    Row(
      Cell.arg,
      Cell.arg,
      Cell.arg
    ).withId("row")
  ).withId("sheet")

  private val template = Template
    .sheet(sheet)
    .fixate()

  test("2 rows with 1 multiple arg") {
    val data = Seq(
      Data(1, 2, 3),
      Data(4, Seq(5, 51, 52), 6)
    )

    val sheets: Seq[PrepSheet] = template
      .extend()
      .row("sheet", "row", data)
      .prepare()

    assert(sheets.head.rows.size == 2)

    val headRow = sheets.head.rows.head
    assert(headRow.cells.count(_.inOneCopy) == 0)
    assert(headRow.cells.size == 3)
    assert {
      headRow.cells.flatMap(_.value) == Seq(1, 2, 3).map(Value.convertToCellValue[Int])
    }

    val lastRow = sheets.head.rows.last
    assert(lastRow.cells.count(_.inOneCopy) == 0)
    assert(lastRow.cells.size == 5)
    assert {
      lastRow.cells.flatMap(_.value) == Seq(4, 5, 51, 52, 6).map(Value.convertToCellValue[Int])
    }
  }

  test("2 rows with 4 args in second row") {
    val data = Seq(
      Data(1, 2, 3),
      Data(4, 5, 6, 7)
    )

    val sheets: Seq[PrepSheet] = template
      .extend()
      .row("sheet", "row", data)
      .prepare()

    assert(sheets.head.rows.size == 2)

    val headRow = sheets.head.rows.head
    assert(headRow.cells.count(_.inOneCopy) == 0)
    assert(headRow.cells.size == 3)
    assert {
      headRow.cells.flatMap(_.value) == Seq(1, 2, 3).map(Value.convertToCellValue[Int])
    }

    val lastRow = sheets.head.rows.last
    assert(lastRow.cells.count(_.inOneCopy) == 0)
    assert(lastRow.cells.size == 3)
    assert {
      lastRow.cells.flatMap(_.value) == Seq(4, 5, 6).map(Value.convertToCellValue[Int])
    }
  }

  test("2 rows with 2 args in second row") {
    val data = Seq(
      Data(1, 2, 3),
      Data(4, 5)
    )

    val sheets: Seq[PrepSheet] = template
      .extend()
      .row("sheet", "row", data)
      .prepare()

    assert(sheets.head.rows.size == 2)

    val headRow = sheets.head.rows.head
    assert(headRow.cells.count(_.inOneCopy) == 0)
    assert(headRow.cells.size == 3)
    assert {
      headRow.cells.flatMap(_.value) == Seq(1, 2, 3).map(Value.convertToCellValue[Int])
    }

    val lastRow = sheets.head.rows.last
    assert(lastRow.cells.count(_.inOneCopy) == 0)
    assert(lastRow.cells.size == 3)
    assert {
      lastRow.cells.flatMap(_.value) == Seq(Value.IntVal(4), Value.IntVal(5), Value.StrVal(ErrorMsg.noCellArg))
    }
  }

  test("2 rows with 3 simple args") {
    val data = Seq(
      Data(1, 2, 3),
      Data(4, 5, 6)
    )

    val sheets: Seq[PrepSheet] = template
      .extend()
      .row("sheet", "row", data)
      .prepare()

    assert(sheets.head.rows.size == 2)

    val headRow = sheets.head.rows.head
    assert(headRow.cells.count(_.inOneCopy) == 0)
    assert(headRow.cells.size == 3)
    assert {
      headRow.cells.flatMap(_.value) == Seq(1, 2, 3).map(Value.convertToCellValue[Int])
    }

    val lastRow = sheets.head.rows.last
    assert(lastRow.cells.size == 3)
    assert(lastRow.cells.count(_.inOneCopy) == 0)
    assert {
      lastRow.cells.flatMap(_.value) == Seq(4, 5, 6).map(Value.convertToCellValue[Int])
    }
  }
}
