package com.nanikin.ezxlsx

import scala.collection.mutable

private[ezxlsx] sealed trait Pos

private[ezxlsx] object Pos {
  sealed trait Key extends Pos

  object Key {
    sealed trait XY extends Key

    final case class X(v: Int) extends XY
    final case class Y(v: Int) extends XY

    final case class Id(v: String) extends Key
  }

  sealed trait Value extends Pos

  object Value {
    sealed trait TrulyImmutable extends Value
    sealed trait TrulyMutable extends Value

    final case class XYMutableMap(v: mutable.HashMap[String, Seq[Int]]) extends TrulyMutable
    final case class XYMap(v: Map[String, Seq[Int]]) extends TrulyImmutable
    final case class XYPos(x: Int, y: Int) extends TrulyMutable with TrulyImmutable
  }
}
