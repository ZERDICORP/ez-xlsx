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
        Data("Russia", 140000000).withNested(
          Data("Moscow", 13000000),
          Data("Saint-Petersburg", 5000000)
        ),
        Data("USA", 331000000).withNested(
          Data("New York", 8500000),
          Data("Los Angeles", 4000000)
        ),
        Data("Japan", 126000000).withNested(
          Data("Tokyo", 9200000),
          Data("Osaka", 2700000)
        )
      )
    )
    .interpret()
    .save("examples/apache-poi/src/main/out/NestedTable.xlsx")
}
