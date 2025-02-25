import com.nanikin.ezxlsx._
import com.nanikin.ezxlsx.apachepoi._

object SimpleStyledTable extends App {

  val styles: Seq[Class] = Seq(
    Class.static("table-header")(
      Style.TextBold,
      Style.Align.V.Center,
      Style.Align.H.Center,
      Style.FontSize(12)
    ),
    Class.static("horse-power")(Style.Align.H.Center),
    Class.byVal("bmw-color") {
      case "BMW" =>
        Seq(
          Style.BgColorHex("#008000"),
          Style.TextColorHex("#FFFFFF")
        )
      case _ =>
        Seq(
          Style.BgColorHex("#FFFFFF"),
          Style.TextColorHex("#000000")
        )
    },
    Class.byVal("generic-color") {
      case "Purple" =>
        Seq(
          Style.BgColorHex("#A020F0"),
          Style.TextColorHex("#FFFFFF")
        )
      case "Red" =>
        Seq(
          Style.BgColorHex("#FF0000"),
          Style.TextColorHex("#FFFFFF")
        )
      case "Silver" =>
        Seq(
          Style.BgColorHex("#C0C0C0"),
          Style.TextColorHex("#000000")
        )
      case "Blue" =>
        Seq(
          Style.BgColorHex("#0000FF"),
          Style.TextColorHex("#FFFFFF")
        )
      case "White" =>
        Seq(
          Style.BgColorHex("#FFFFFF"),
          Style.TextColorHex("#000000")
        )
      case "Black" =>
        Seq(
          Style.BgColorHex("#000000"),
          Style.TextColorHex("#FFFFFF")
        )
      case _ =>
        Seq(
          Style.BgColorHex("#FFFFFF"),
          Style.TextColorHex("#000000")
        )
    }
  )

  val sheet = Sheet(
    Row(
      Cell("Car Name").withClasses("table-header"),
      Cell("Horse Power").withClasses("table-header"),
      Cell("Color").withClasses("table-header")
    ).withSettings(height = 30),
    Row(
      Cell.arg.withClasses("bmw-color"),
      Cell.arg.withClasses("horse-power"),
      Cell.arg.withClasses("generic-color")
    ).withId("cars-list")
  )
    .withName("Cars")
    .withId("cars-sheet")
    .withCols(
      Col(width = 15),
      Col(width = 15),
      Col(width = 15)
    )
    .withStyles(styles)

  val template = Template
    .sheet(sheet)
    .fixate()

  template
    .extend()
    .row(
      sheetId = "cars-sheet",
      rowId = "cars-list",
      data = Seq(
        Data("BMW", 800, "Purple"),
        Data("Audi", 350, "Red"),
        Data("Mercedes", 450, "Silver"),
        Data("Toyota", 300, "Blue"),
        Data("Ford", 370, "White"),
        Data("Tesla", 500, "Black")
      )
    )
    .interpret()
    .save("examples/apache-poi/src/main/out/SimpleStyledTable.xlsx")
}
