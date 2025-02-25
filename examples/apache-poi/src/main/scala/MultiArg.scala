import com.nanikin.ezxlsx._
import com.nanikin.ezxlsx.apachepoi._

object MultiArg extends App {

  val sheet = Sheet(
    Row(
      Cell.arg.withClasses("test").withId("arg"),
      Cell("SUM(%s)" << "arg"),
      Cell("COUNTIF(%s,0)" << "arg")
    ).withSettings(height = 30).withId("row"),
    Row(
      Cell.arg.withClasses("test").withId("arg"),
      Cell("SUM(%s)" << "arg"),
      Cell("COUNTIF(%s,0)" << "arg")
    ).withSettings(height = 30).withId("row2")
  )
    .withId("sheet")

  val template = Template.sheet(sheet).fixate()

  val data = Seq(Data(Seq(1, 0, 3)))
  val data2 = Seq(Data(Seq(0, 2, 0)))

  template
    .extend()
    .row(sheetId = "sheet", rowId = "row", data)
    .row(sheetId = "sheet", rowId = "row2", data2)
    .interpret()
    .save("examples/apache-poi/src/main/out/MultiArg.xlsx")
}
