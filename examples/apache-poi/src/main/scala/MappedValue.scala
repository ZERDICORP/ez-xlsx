import com.nanikin.ezxlsx._
import com.nanikin.ezxlsx.apachepoi._

object MappedValue extends App {

  val mapper = ValueMapper {
    case true => "Fool"
    case false => "Cool"
  }

  val sheet = Sheet(
    Row(
      Cell("Name"),
      Cell("Status")
    ).withSettings(height = 30),
    Row(
      Cell.arg,
      Cell.arg.withMapper(mapper)
    ).withId("list")
  )
    .withName("Boys")
    .withId("boys")
    .withCols(
      Col(width = 15),
      Col(width = 15)
    )
    .withFreeze(rows = 1)

  val template = Template.sheet(sheet).fixate()

  val data = Seq(
    Data("Gilfoyle", false),
    Data("Alex", true),
    Data("David", true),
    Data("Dinesh", false)
  )

  template
    .extend()
    .row(sheetId = "boys", rowId = "list", data)
    .interpret()
    .save("examples/apache-poi/src/main/out/MappedValue.xlsx")
}
