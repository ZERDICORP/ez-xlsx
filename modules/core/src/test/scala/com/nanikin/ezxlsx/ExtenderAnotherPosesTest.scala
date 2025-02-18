package com.nanikin.ezxlsx

import com.nanikin.ezxlsx.prep.PrepSheet
import com.nanikin.ezxlsx.prep.realRowsLen
import org.scalatest.funsuite.AnyFunSuite

class ExtenderAnotherPosesTest extends AnyFunSuite {

  private val sheet = Sheet(
    Row(
      Cell("Name"),
      Cell("Age")
    ),
    Row(
      Cell.arg,
      Cell.arg.withId("age")
    )
      .withId("row")
      .withNested(
        Row.nested(
          Cell.arg,
          Cell.arg.withId("age")
        )
      )
  ).withId("sheet")

  private val template = Template
    .sheet(sheet)
    .fixate()

  test("poses collected right") {
    val data = Seq(
      Data("Alex", 15).withNested(
        Data("Bob", 12)
      ),
      Data("Marry", 18).withNested(
        Data("Michael", 10)
      ),
      Data("Josh", 35),
      Data("Carol", 30).withNested(
        Data("Marty", 22)
      )
    )

    val sheets = template
      .extend()
      .row("sheet", "row", data)
      .prepare()

    val poses = sheets.head.poses

    assert(poses.size == 8)

    assert {
      poses == Map(
        Pos.Key.X(1) -> Pos.Value.XYMap(
          Map(
            "age" -> Seq(1, 2, 3, 4, 5, 6, 7)
          )
        ),
        Pos.Key.Y(1) -> Pos.Value.XYMap(
          Map(
            "age" -> Seq(1)
          )
        ),
        Pos.Key.Y(2) -> Pos.Value.XYMap(
          Map(
            "age" -> Seq(1)
          )
        ),
        Pos.Key.Y(3) -> Pos.Value.XYMap(
          Map(
            "age" -> Seq(1)
          )
        ),
        Pos.Key.Y(4) -> Pos.Value.XYMap(
          Map(
            "age" -> Seq(1)
          )
        ),
        Pos.Key.Y(5) -> Pos.Value.XYMap(
          Map(
            "age" -> Seq(1)
          )
        ),
        Pos.Key.Y(6) -> Pos.Value.XYMap(
          Map(
            "age" -> Seq(1)
          )
        ),
        Pos.Key.Y(7) -> Pos.Value.XYMap(
          Map(
            "age" -> Seq(1)
          )
        )
      )
    }
  }

  test("all XY are right") {
    val data = Seq(
      Data("Alex", 15).withNested(
        Data("Bob", 12)
      ),
      Data("Marry", 18).withNested(
        Data("Michael", 10)
      ),
      Data("Josh", 35),
      Data("Carol", 30).withNested(
        Data("Marty", 22)
      )
    )

    val sheets: Seq[PrepSheet] = template
      .extend()
      .row("sheet", "row", data)
      .prepare()

    assert(realRowsLen(sheets.head.rows) == 8)

    val header = sheets.head.rows.head
    assert(header.cells.size == 2)
    assert {
      header.cells.map(_.xy) == Seq((0, 0), (1, 0))
    }

    val ext1 = sheets.head.rows(1)
    assert(ext1.cells.size == 2)
    assert {
      ext1.cells.map(_.xy) == Seq((0, 1), (1, 1))
    }
    assert(ext1.nested.head.cells.size == 2)
    assert {
      ext1.nested.head.cells.map(_.xy) == Seq((0, 2), (1, 2))
    }

    val ext2 = sheets.head.rows(2)
    assert(ext2.cells.size == 2)
    assert {
      ext2.cells.map(_.xy) == Seq((0, 3), (1, 3))
    }
    assert(ext2.nested.head.cells.size == 2)
    assert {
      ext2.nested.head.cells.map(_.xy) == Seq((0, 4), (1, 4))
    }

    val ext3 = sheets.head.rows(3)
    assert(ext3.cells.size == 2)
    assert {
      ext3.cells.map(_.xy) == Seq((0, 5), (1, 5))
    }

    val ext4 = sheets.head.rows(4)
    assert(ext4.cells.size == 2)
    assert {
      ext4.cells.map(_.xy) == Seq((0, 6), (1, 6))
    }
    assert(ext4.nested.head.cells.size == 2)
    assert {
      ext4.nested.head.cells.map(_.xy) == Seq((0, 7), (1, 7))
    }
  }
}
