import com.nanikin.ezxlsx.Value.Formula
import com.nanikin.ezxlsx._
import com.nanikin.ezxlsx.apachepoi._

object Formulas extends App {

  val sheet = Sheet(
    Row(
      Cell.empty,
      Cell("Product"),
      Cell("Price"),
      Cell("Tax"),
      Cell("Tax Percent")
    ).withSettings(height = 30),
    Row(
      Cell.empty,
      Cell.arg,
      Cell.arg.withId("price-value"),
      Cell.arg.withId("tax-value"),
      Cell(Formula.ratio("tax-value", "price-value"))
    ).withId("product-list"),
    Row(
      Cell("Total"),
      Cell.empty,
      Cell(Formula.sum("price-value")).withId("price-total"),
      Cell(Formula.sum("tax-value")).withId("tax-total"),
      Cell(Formula.ratio("tax-total", "price-total"))
    ).withSettings(height = 30)
  )
    .withName("Cart")
    .withId("cart-sheet")
    .withColsWidth(
      1 -> 15,
      2 -> 15,
      3 -> 15,
      4 -> 15
    )

  val template = Template
    .sheet(sheet)
    .fixate()

  template
    .extend()
    .row(
      sheetId = "cart-sheet",
      rowId = "product-list",
      data = Seq(
        Data("Milk", 100, 15),
        Data("Beer", 400, 30),
        Data("Apples", 500, 100)
      )
    )
    .interpret()
    .save("examples/apache-poi/src/main/out/Formulas.xlsx")
}
