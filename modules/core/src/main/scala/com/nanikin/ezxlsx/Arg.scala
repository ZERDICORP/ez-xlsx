package com.nanikin.ezxlsx

sealed trait Arg {
  val classes: Seq[String]
}

object Arg {
  private[ezxlsx] final case class Default(v: Value, classes: Seq[String] = Seq.empty) extends Arg
  private[ezxlsx] final case class Many(v: Seq[Value], classes: Seq[String] = Seq.empty) extends Arg

  implicit def defaultConverter[T](value: T)(implicit converter: Value.Converter[T]): Arg =
    Default(converter.convert(value))

  implicit def manyConverter[T](value: Seq[T])(implicit converter: Value.Converter[T]): Arg =
    Many(value.map(converter.convert))

  private def extendClasses(arg: Arg, classes: Seq[String]): Arg =
    arg match {
      case default: Default => default.copy(classes = arg.classes ++ classes)
      case many: Many => many.copy(classes = arg.classes ++ classes)
    }

  implicit class ops(arg: Arg) {
    def withClasses(classes: String*): Arg = extendClasses(arg, classes)
  }

  implicit class opsT[T: Value.Converter](v: T) {
    def withClasses(classes: String*): Arg = extendClasses(defaultConverter(v), classes)
  }

  implicit class opsSeqT[T: Value.Converter](seq: Seq[T]) {
    def withClasses(classes: String*): Arg = extendClasses(manyConverter(seq), classes)
  }
}
