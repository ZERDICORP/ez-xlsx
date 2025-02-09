package com.nanikin.ezxlsx

sealed trait Class {
  val className: String
}

object Class {
  private[ezxlsx] final case class Static(className: String, styles: Seq[Style]) extends Class
  private[ezxlsx] final case class Dependent(className: String, styles: Value => Seq[Style]) extends Class
  private[ezxlsx] final case class Raw(className: String, styles: (Any, Value) => Unit) extends Class

  def static(className: String)(styles: Style*): Static = Static(className, styles)
  def byValue(className: String)(styles: Value => Seq[Style]): Dependent = Dependent(className, styles)
  def raw(className: String)(styles: (Any, Value) => Unit): Raw = Raw(className, styles)
}
