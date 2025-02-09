package com.nanikin.ezxlsx

import com.nanikin.ezxlsx.prep.PrepSheet
import org.scalatest.funsuite.AnyFunSuite

class ExtenderPosesTest extends AnyFunSuite {

  private val sheet = Sheet(
    Row(
      Cell("name").withId("name"),
      Cell("arg")
    ),
    Row(
      Cell.arg.withId("arg1"),
      Cell.arg.withId("arg2"),
      Cell.arg
    ).withId("row"),
    Row.empty
      .withId("nest")
      .withNested(
        Row.nested(Cell.arg)
      )
  ).withId("sheet")

  private val template = Template
    .sheet(sheet)
    .fixate()

  test("poses collected right") {
    val data = Seq(
      Data(1, 2, 3),
      Data(4, Seq(5, 51, 52), 6)
    )
    val nestData = Seq(
      Data.empty.withNested(
        Data(666)
      )
    )

    val sheets = template
      .extend()
      .row("sheet", "row", data)
      .row("sheet", "nest", nestData)
      .prepare()

    val poses = sheets.head.poses

    assert(poses.size == 7)

    assert {
      poses == Map(
        Pos.Key.Id("name") -> Pos.Value.XYPos(0, 0),
        Pos.Key.X(0) -> Pos.Value.XYMap(
          Map(
            "arg1" -> Seq(1, 2)
          )
        ),
        Pos.Key.X(1) -> Pos.Value.XYMap(
          Map(
            "arg2" -> Seq(1, 2)
          )
        ),
        Pos.Key.X(2) -> Pos.Value.XYMap(
          Map(
            "arg2" -> Seq(2)
          )
        ),
        Pos.Key.X(3) -> Pos.Value.XYMap(
          Map(
            "arg2" -> Seq(2)
          )
        ),
        Pos.Key.Y(1) -> Pos.Value.XYMap(
          Map(
            "arg1" -> Seq(0),
            "arg2" -> Seq(1)
          )
        ),
        Pos.Key.Y(2) -> Pos.Value.XYMap(
          Map(
            "arg1" -> Seq(0),
            "arg2" -> Seq(1, 2, 3)
          )
        )
      )
    }
  }

  test("all XY are right") {
    val data = Seq(
      Data(1, 2, 3),
      Data(4, Seq(5, 51, 52), 6)
    )
    val nestData = Seq(
      Data.empty.withNested(
        Data(666)
      )
    )

    val sheets: Seq[PrepSheet] = template
      .extend()
      .row("sheet", "row", data)
      .row("sheet", "nest", nestData)
      .prepare()

    assert(sheets.head.rows.size == 4)

    val header = sheets.head.rows.head
    assert(header.cells.size == 2)
    assert {
      header.cells.map(_.xy) == Seq((0, 0), (1, 0))
    }

    val ext1 = sheets.head.rows(1)
    assert(ext1.cells.size == 3)
    assert {
      ext1.cells.map(_.xy) == Seq((0, 1), (1, 1), (2, 1))
    }

    val ext2 = sheets.head.rows(2)
    assert(ext2.cells.size == 5)
    assert {
      ext2.cells.map(_.xy) == Seq((0, 2), (1, 2), (2, 2), (3, 2), (4, 2))
    }

    val nest = sheets.head.rows(3).nested.head
    assert(nest.cells.size == 1)
    assert {
      nest.cells.map(_.xy) == Seq((0, 4))
    }
  }
}
