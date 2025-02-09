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

    private[ezxlsx] sealed trait Formula extends Value

    object Formula {
      private[ezxlsx] final case class CellRef(id: String) extends Formula
      private[ezxlsx] final case class RangeRef(fromId: String, toId: String) extends Formula
      private[ezxlsx] final case class Sum(args: Seq[Formula]) extends Formula
      private[ezxlsx] final case class Avg(args: Seq[Formula]) extends Formula
      private[ezxlsx] final case class Ratio(numerator: CellRef, denominator: CellRef) extends Formula

      def sum(args: Formula*): Sum = Sum(args)
      def avg(args: Formula*): Avg = Avg(args)
      def ratio(of: CellRef, by: CellRef): Ratio = Ratio(of, by)

      implicit def cellRefConverter(ref: String): CellRef = CellRef(ref)
      implicit def rangeRefConverter(ref: (String, String)): RangeRef = RangeRef(ref._1, ref._2)
    }

    trait Converter[T] {
      def convert(value: T): Value
    }

    object Converter {
      implicit val _String: Converter[String] = (value: String) => StrVal(value)
      implicit val _Int: Converter[Int] = (value: Int) => IntVal(value)
    }

    implicit def convertToCellValue[T](value: T)(implicit converter: Converter[T]): Value =
      converter.convert(value)
  }
}
