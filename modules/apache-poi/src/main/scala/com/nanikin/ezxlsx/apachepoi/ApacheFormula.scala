package com.nanikin.ezxlsx.apachepoi

import cats.Show
import cats.implicits.catsSyntaxEitherId
import cats.implicits.toShow
import com.nanikin.ezxlsx.ErrorMsg
import com.nanikin.ezxlsx.Pos
import com.nanikin.ezxlsx.PosMap
import com.nanikin.ezxlsx.Value
import com.nanikin.ezxlsx.Value.Formula

private[apachepoi] object ApacheFormula {

  private sealed trait ResolvedRef

  private object ResolvedRef {
    final case class One(ref: String) extends ResolvedRef
    final case class Range(left: String, right: String) extends ResolvedRef

    implicit val show: Show[ResolvedRef] = Show.show {
      case One(ref) => ref
      case Range(left, right) => f"$left:$right"
    }
  }

  private def col(colIndex: Int): String = {
    var index = colIndex
    var colName = ""
    while (index >= 0) {
      colName = ('A' + (index % 26)).toChar + colName
      index = (index / 26) - 1
    }
    colName
  }

  private def ref(xy: (Int, Int)): String =
    s"${col(xy._1)}${xy._2 + 1}"

  def resolve(formula: Value.Formula, cellXY: (Int, Int), poses: PosMap): Either[String, String] = {
    def resolveRef(id: String): Option[ResolvedRef] = {
      val (cx, cy) = cellXY

      def fromYAxis: Option[ResolvedRef] =
        poses.get(Pos.Key.Y(cy)).flatMap { case Pos.Value.XYMap(map) =>
          map.get(id).map {
            case Seq(headX) => ResolvedRef.One(ref(headX, cy))
            case seq =>
              ResolvedRef.Range(
                ref(seq.head, cy),
                ref(seq.last, cy)
              )
          }
        }

      def fromXAxis: Option[ResolvedRef] =
        poses.get(Pos.Key.X(cx)).flatMap { case Pos.Value.XYMap(map) =>
          map.get(id).map {
            case Seq(headY) => ResolvedRef.One(ref(cx, headY))
            case seq =>
              ResolvedRef.Range(
                ref(cx, seq.head),
                ref(cx, seq.last)
              )
          }
        }

      def fromGlobal: Option[ResolvedRef] =
        poses.get(Pos.Key.Id(id)).map { case Pos.Value.XYPos(x, y) =>
          ResolvedRef.One(ref(x, y))
        }

      fromYAxis.orElse(fromXAxis).orElse(fromGlobal)
    }

    def loop(formula: Value.Formula): Either[String, String] = {
      def mkArgs(args: Seq[Value.Formula]): Either[String, String] = {
        val (lefts, rights) = args.map(loop).partitionMap(identity)
        if (lefts.nonEmpty) lefts.mkString(", ").asLeft
        else rights.mkString(", ").asRight
      }

      formula match {
        case Formula.CellRef(id) =>
          resolveRef(id) match {
            case Some(resolved) => resolved.show.asRight
            case None => ErrorMsg.idNotFound(id).asLeft
          }
        case Formula.RangeRef(fromId, toId) =>
          (resolveRef(fromId), resolveRef(toId)) match {
            case (Some(left: ResolvedRef.One), Some(right: ResolvedRef.One)) =>
              f"$left:$right".asRight
            case (None, Some(_)) => ErrorMsg.idNotFound(fromId).asLeft
            case (Some(_), None) => ErrorMsg.idNotFound(toId).asLeft
            case _ => ErrorMsg.wrongRangeRef.asLeft
          }
        case Formula.Sum(args) => mkArgs(args).map(x => f"SUM($x)")
        case Formula.Avg(args) => mkArgs(args).map(x => f"AVERAGE($x)")
        case Formula.Ratio(numerator, denominator) =>
          for {
            n <- loop(numerator)
            d <- loop(denominator)
          } yield f"($n / $d) * 100"
      }
    }

    loop(formula)
  }
}
