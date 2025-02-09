package com.nanikin.ezxlsx

private[ezxlsx] final case class Data private (
    private[ezxlsx] val args: Seq[Arg],
    private[ezxlsx] val nested: Seq[Data] = Seq.empty
)

object Data {
  def apply(args: Arg*): Data = Data(args)
  def empty: Data = Data(Seq.empty)

  implicit class ops(data: Data) {
    def withNested(nested: Data*): Data = data.copy(nested = nested)
  }
}
