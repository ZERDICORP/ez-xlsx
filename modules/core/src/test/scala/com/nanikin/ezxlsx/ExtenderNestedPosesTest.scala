package com.nanikin.ezxlsx

import org.scalatest.funsuite.AnyFunSuite

class ExtenderNestedPosesTest extends AnyFunSuite {

  private val sheet = Sheet(
    Row(
      Cell.arg,
      Cell.arg.withId("age")
    )
      .withId("row")
      .withNested(
        Row.nested(
          Cell.arg,
          Cell.arg.withId("age-nested")
        )
      )
  ).withId("sheet")

  private val template = Template
    .sheet(sheet)
    .fixate()

  test("poses collected right") {
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

    val sheets = template
      .extend()
      .row("sheet", "row", data)
      .prepare()

    val poses = sheets.head.poses

    assert(poses.size == 11)

    assert(
      poses == Map(
        Pos.Key.X(1) -> Pos.Value.XYMap(
          Map(
            "age" -> Seq(0, 3, 6, 7),
            "age-nested" -> Seq(1, 2, 4, 5, 8, 9)
          )
        ),
        Pos.Key.Y(0) -> Pos.Value.XYMap(
          Map(
            "age" -> Seq(1)
          )
        ),
        Pos.Key.Y(1) -> Pos.Value.XYMap(
          Map(
            "age-nested" -> Seq(1)
          )
        ),
        Pos.Key.Y(2) -> Pos.Value.XYMap(
          Map(
            "age-nested" -> Seq(1)
          )
        ),
        Pos.Key.Y(3) -> Pos.Value.XYMap(
          Map(
            "age" -> Seq(1)
          )
        ),
        Pos.Key.Y(4) -> Pos.Value.XYMap(
          Map(
            "age-nested" -> Seq(1)
          )
        ),
        Pos.Key.Y(5) -> Pos.Value.XYMap(
          Map(
            "age-nested" -> Seq(1)
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
        ),
        Pos.Key.Y(8) -> Pos.Value.XYMap(
          Map(
            "age-nested" -> Seq(1)
          )
        ),
        Pos.Key.Y(9) -> Pos.Value.XYMap(
          Map(
            "age-nested" -> Seq(1)
          )
        )
      )
    )
  }
}
