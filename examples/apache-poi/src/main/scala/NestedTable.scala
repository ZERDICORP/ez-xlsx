import com.nanikin.ezxlsx._
import com.nanikin.ezxlsx.apachepoi._

object NestedTable extends App {

  val sheet = Sheet(
    Row(
      Cell("Country/City"),
      Cell("Population")
    ).withSettings(height = 30),
    Row(
      Cell.arg,
      Cell.arg
    ).withId("list")
      .withNested(
        Row.nested(
          Cell.arg,
          Cell.arg
        )
      )
  )
    .withName("Population")
    .withId("population-sheet")
    .withColsWidth(
      0 -> 15,
      1 -> 15
    )

  val template = Template
    .sheet(sheet)
    .fixate()

  template
    .extend()
    .row(
      sheetId = "population-sheet",
      rowId = "list",
      data = Seq(
        Data("Russia", 140_000_000).withNested(
          Data("Moscow", 13_000_000),
          Data("Saint-Petersburg", 5_000_000)
        ),
        Data("USA", 331_000_000).withNested(
          Data("New York", 8_500_000),
          Data("Los Angeles", 4_000_000)
        ),
        Data("Japan", 126_000_000).withNested(
          Data("Tokyo", 9_200_000),
          Data("Osaka", 2_700_000)
        )
      )
    )
    .interpret()
    .save("examples/apache-poi/src/main/out/NestedTable.xlsx")
}
