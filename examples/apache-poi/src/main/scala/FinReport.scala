import com.nanikin.ezxlsx._
import com.nanikin.ezxlsx.apachepoi._

object FinReport extends App {

  val styles = Seq(
    Class.static("header")(
      Style.Align.H.Center,
      Style.Align.V.Center,
      Style.BgColorHex("#4F81BD"),
      Style.TextColorHex("#FFFFFF")
    ),
    Class.byPos("list-item") { case (_, (_, y)) =>
      Seq(if (y % 2 == 0) {
        Style.BgColorHex("#DCE6F1")
      } else Style.BgColorHex("#FFFFFF"))
    },
    Class.static("footer")(
      Style.Align.H.Center,
      Style.Align.V.Center,
      Style.BgColorHex("#C0504D"),
      Style.TextColorHex("#FFFFFF")
    )
  )

  val sheet = Sheet(
    Row(
      Cell("Date"),
      Cell("Category"),
      Cell("Income ($)"),
      Cell("Expenses ($)"),
      Cell("Total ($)")
    )
      .withSettings(height = 30)
      .withClasses("header"),
    Row(
      Cell.arg,
      Cell.arg,
      Cell.arg.withId("income"),
      Cell.arg.withId("expense"),
      Cell { "%s - %s" << ("income", "expense") }
    )
      .withId("list")
      .withClasses("list-item"),
    Row(
      Cell("Total"),
      Cell.empty,
      Cell("SUM(%s)" << "income").withId("incomes-total"),
      Cell("SUM(%s)" << "expense").withId("expenses-total"),
      Cell("%s - %s" << ("incomes-total", "expenses-total"))
    )
      .withSettings(height = 30)
      .withClasses("footer")
  )
    .withName("Financial Report")
    .withId("fin-report")
    .withColsWidth(
      15,
      15,
      12,
      12,
      12
    )
    .withStyles(styles)
    .withFreeze(cols = 2, rows = 1)

  val template = Template.sheet(sheet).fixate()

  val data = Seq(
    Data("2025-01-01", "Sales", 10000, 5000),
    Data("2025-01-02", "Marketing", 0, 1500),
    Data("2025-01-03", "Subscriptions", 2000, 0),
    Data("2025-01-04", "Infrastructure", 0, 3000),
    Data("2025-01-05", "Consulting", 5000, 1000),
    Data("2025-01-06", "Customer Support", 0, 1200),
    Data("2025-01-07", "Research & Development", 0, 4000),
    Data("2025-01-08", "Sponsorship", 3000, 800),
    Data("2025-01-09", "Investments", 7000, 0)
  )

  template
    .extend()
    .row(sheetId = "fin-report", rowId = "list", data)
    .interpret()
    .save("examples/apache-poi/src/main/out/FinReport.xlsx")
}
