import com.nanikin.ezxlsx._
import com.nanikin.ezxlsx.apachepoi.interpreter

object TableWithFormulas extends App {

  val sheet = Sheet(
    Row(
      Cell.empty,
      Cell("Product").withSettings(autoFilter = true),
      Cell("Price"),
      Cell("Tax"),
      Cell("Tax Percent")
    ).withSettings(height = 30),
    Row(
      Cell.empty,
      Cell.arg,
      Cell.arg.withId("price-value"),
      Cell.arg.withId("tax-value"),
      Cell("(%s / %s) * 100" << ("tax-value", "price-value"))
    ).withId("product-list"),
    Row(
      Cell("Total"),
      Cell.empty,
      Cell("SUBTOTAL(9,%s)" << "price-value").withId("price-total"),
      Cell("SUBTOTAL(9,%s)" << "tax-value").withId("tax-total"),
      Cell("(%s / %s) * 100" << ("tax-total", "price-total"))
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
    .save("examples/apache-poi/src/main/out/TableWithFormulas.xlsx")
}
