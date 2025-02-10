package com.nanikin

import scala.collection.mutable
import scala.language.implicitConversions

package object ezxlsx {

  private[ezxlsx] type PosMapMutable = mutable.HashMap[Pos.Key, Pos.Value.TrulyMutable]
  private[ezxlsx] type PosMap = Map[Pos.Key, Pos.Value.TrulyImmutable]

  sealed trait Value

  object Value {
    final case class StrVal(v: String) extends Value
    final case class IntVal(v: Int) extends Value
    final case class Formula(v: String, ids: Seq[String]) extends Value

    implicit class FormulaOps(v: String) {
      def wiz(ids: String*): Formula = Formula(v, ids)
    }

    trait Converter[T] {
      def convert(value: T): Value
    }

    object Converter {

      implicit val _String: Converter[String] = new Converter[String] {
        override def convert(value: String): Value = StrVal(value)
      }

      implicit val _Int: Converter[Int] = new Converter[Int] {
        override def convert(value: Int): Value = IntVal(value)
      }
    }

    implicit def convertToCellValue[T](value: T)(implicit converter: Converter[T]): Value =
      converter.convert(value)
  }
}
