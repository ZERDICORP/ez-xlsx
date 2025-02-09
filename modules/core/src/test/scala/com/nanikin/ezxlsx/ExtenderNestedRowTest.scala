package com.nanikin.ezxlsx

import com.nanikin.ezxlsx.prep.PrepSheet
import org.scalatest.funsuite.AnyFunSuite

class ExtenderNestedRowTest extends AnyFunSuite {

  private val sheet = Sheet(
    Row(Cell.arg)
      .withId("row")
      .withNested(
        Row.nested(Cell.arg)
      )
  ).withId("sheet")

  private val template = Template
    .sheet(sheet)
    .fixate()

  test("1 row with 2 nested rows") {
    val data = Seq(
      Data(
        args = Seq(Arg.Default(1)),
        nested = Seq(
          Data(args = Seq(Arg.Default(12))),
          Data(args = Seq(Arg.Default(13)))
        )
      )
    )

    val extended: Seq[PrepSheet] = template
      .extend()
      .row("sheet", "row", data)
      .prepare()

    assert(extended.head.rows.size == 1)

    val row = extended.head.rows.head
    assert(row.cells.size == 1)
    assert(row.cells.count(_.inOneCopy) == 1)
    assert {
      row.cells.flatMap(_.value) == Seq(1).map(Value.convertToCellValue[Int])
    }

    assert(row.nested.size == 2)

    val headNested = row.nested.head
    assert(headNested.cells.size == 1)
    assert(headNested.cells.count(_.inOneCopy) == 0)
    assert {
      headNested.cells.flatMap(_.value) == Seq(12).map(Value.convertToCellValue[Int])
    }

    val lastNested = row.nested.last
    assert(lastNested.cells.size == 1)
    assert(lastNested.cells.count(_.inOneCopy) == 0)
    assert {
      lastNested.cells.flatMap(_.value) == Seq(13).map(Value.convertToCellValue[Int])
    }
  }
}
