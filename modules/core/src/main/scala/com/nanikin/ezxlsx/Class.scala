package com.nanikin.ezxlsx

sealed trait Class {
  val className: String
}

object Class {
  private[ezxlsx] final case class Static(className: String, styles: Seq[Style]) extends Class
  private[ezxlsx] final case class ValDependent(className: String, styles: Any => Seq[Style]) extends Class

  private[ezxlsx] final case class PosDependent(className: String, styles: (Any, (Int, Int)) => Seq[Style])
      extends Class
  private[ezxlsx] final case class Raw(className: String, styles: (Any, Any) => Unit) extends Class

  def static(className: String)(styles: Style*): Static = Static(className, styles)
  def byVal(className: String)(styles: Any => Seq[Style]): ValDependent = ValDependent(className, styles)
  def byPos(className: String)(styles: (Any, (Int, Int)) => Seq[Style]): PosDependent = PosDependent(className, styles)
  def raw(className: String)(styles: (Any, Any) => Unit): Raw = Raw(className, styles)
}
