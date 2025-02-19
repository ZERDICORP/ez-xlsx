package com.nanikin.ezxlsx

final case class Data private (
    args: Seq[Arg],
    nested: Seq[Data] = Seq.empty,
    classes: Seq[String] = Seq.empty
)

object Data {
  def apply(args: Arg*): Data = Data(args)
  def empty: Data = Data(Seq.empty)

  implicit class ops(data: Data) {
    def withNested(nested: Data*): Data = data.copy(nested = nested)
    def withClasses(classes: String*): Data = data.copy(classes = classes)
  }
}
