package com.nanikin.ezxlsx

private[ezxlsx] sealed trait Arg

object Arg {
  private[ezxlsx] final case class Default(v: Value) extends Arg
  private[ezxlsx] final case class Many(v: Seq[Value]) extends Arg

  implicit def defaultConverter[T](value: T)(implicit converter: Value.Converter[T]): Arg =
    Default(converter.convert(value))

  implicit def manyConverter[T](value: Seq[T])(implicit converter: Value.Converter[T]): Arg =
    Many(value.map(converter.convert))
}
