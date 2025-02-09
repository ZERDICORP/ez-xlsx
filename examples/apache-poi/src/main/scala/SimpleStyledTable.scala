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
    Class.byValue("bmw-color") {
      case Value.StrVal("BMW") =>
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
    Class.byValue("generic-color") {
      case Value.StrVal("Purple") =>
        Seq(
          Style.BgColorHex("#A020F0"),
          Style.TextColorHex("#FFFFFF")
        )
      case Value.StrVal("Red") =>
        Seq(
          Style.BgColorHex("#FF0000"),
          Style.TextColorHex("#FFFFFF")
        )
      case Value.StrVal("Silver") =>
        Seq(
          Style.BgColorHex("#C0C0C0"),
          Style.TextColorHex("#000000")
        )
      case Value.StrVal("Blue") =>
        Seq(
          Style.BgColorHex("#0000FF"),
          Style.TextColorHex("#FFFFFF")
        )
      case Value.StrVal("White") =>
        Seq(
          Style.BgColorHex("#FFFFFF"),
          Style.TextColorHex("#000000")
        )
      case Value.StrVal("Black") =>
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
    .withColsWidth(
      0 -> 15,
      1 -> 15,
      2 -> 15
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
