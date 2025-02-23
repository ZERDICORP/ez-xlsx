package com.nanikin

import scala.collection.mutable
import scala.language.implicitConversions

package object ezxlsx {

  private[ezxlsx] type PosMapMutable = mutable.HashMap[Pos.Key, Pos.Value.TrulyMutable]
  private[ezxlsx] type PosMap = Map[Pos.Key, Pos.Value.TrulyImmutable]

  implicit class FormulaOps(v: String) {
    def <<(ids: String*): Value.Formula = Value.Formula(v, ids)
  }

  sealed trait Value {
    val v: Any
  }

  object Value {
    final case class StrVal(v: String) extends Value
    final case class IntVal(v: Int) extends Value
    final case class DblVal(v: Double) extends Value
    final case class BolVal(v: Boolean) extends Value
    final case class Formula(v: String, ids: Seq[String]) extends Value

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

      implicit val _Dbl: Converter[Double] = new Converter[Double] {
        override def convert(value: Double): Value = DblVal(value)
      }

      implicit val _Bol: Converter[Boolean] = new Converter[Boolean] {
        override def convert(value: Boolean): Value = BolVal(value)
      }
    }

    implicit def convertToCellValue[T](value: T)(implicit converter: Converter[T]): Value =
      converter.convert(value)
  }
}
