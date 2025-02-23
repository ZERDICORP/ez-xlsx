import com.nanikin.ezxlsx._
import com.nanikin.ezxlsx.apachepoi.interpreter

object TableWithFormulas extends App {

  val styles = Seq(
    Class.static("percent")(
      Style.DataFormat("0%"),
      Style.Conditional.BgColorHexByNumber(
        start = (0, "63be7b"),
        mid = (0.5, "ffeb84"),
        end = (1, "f8696b")
      )
    )
  )

  val sheet = Sheet(
    Row(
      Cell.empty,
      Cell("Product").withSettings(autoFilter = true),
      Cell("Price"),
      Cell("Tax"),
      Cell("Tax Percent").withSettings(autoFilter = true)
    ).withSettings(height = 30),
    Row(
      Cell.empty,
      Cell.arg,
      Cell.arg.withId("price-value"),
      Cell.arg.withId("tax-value"),
      Cell("%s / %s" << ("tax-value", "price-value")).withClasses("percent")
    ).withId("product-list"),
    Row(
      Cell("Total"),
      Cell.empty,
      Cell("SUBTOTAL(9,%s)" << "price-value").withId("price-total"),
      Cell("SUBTOTAL(9,%s)" << "tax-value").withId("tax-total"),
      Cell("%s / %s" << ("tax-total", "price-total")).withClasses("percent")
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
    .withStyles(styles)

  val template = Template
    .sheet(sheet)
    .fixate()

  template
    .extend()
    .row(
      sheetId = "cart-sheet",
      rowId = "product-list",
      data = Seq(
        Data("Milk", 100, 87),
        Data("Beer", 400, 30),
        Data("Apples", 500, 450),
        Data("Fish", 57, 3)
      )
    )
    .interpret()
    .save("examples/apache-poi/src/main/out/TableWithFormulas.xlsx")
}
