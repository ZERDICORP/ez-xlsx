package com.nanikin.ezxlsx.apachepoi

import cats.Show
import cats.implicits.catsSyntaxEitherId
import cats.implicits.toShow
import com.nanikin.ezxlsx.ErrorMsg
import com.nanikin.ezxlsx.Pos
import com.nanikin.ezxlsx.PosMap
import com.nanikin.ezxlsx.Utils
import com.nanikin.ezxlsx.Value

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

  def ref(xy: (Int, Int)): String =
    s"${col(xy._1)}${xy._2 + 1}"

  def resolve(formula: Value.Formula, cellXY: (Int, Int), poses: PosMap): Either[String, String] = {
    def resolveRef(id: String): Either[String, String] = {
      val (cx, cy) = cellXY

      def fromYAxis: Option[Seq[ResolvedRef]] =
        poses.get(Pos.Key.Y(cy)).flatMap { case Pos.Value.XYMap(map) =>
          map.get(id).map {
            case Seq(headX) => Seq(ResolvedRef.One(ref(headX, cy)))
            case seq =>
              Seq(
                ResolvedRef.Range(
                  ref(seq.head, cy),
                  ref(seq.last, cy)
                )
              )
          }
        }

      def fromXAxis: Option[Seq[ResolvedRef]] =
        poses.get(Pos.Key.X(cx)).flatMap { case Pos.Value.XYMap(map) =>
          map.get(id).map {
            case Seq(headY) => Seq(ResolvedRef.One(ref(cx, headY)))
            case seq =>
              Utils.jerkily(seq).map {
                case Seq(one) => ResolvedRef.One(ref(cx, one))
                case Seq(from, to) =>
                  ResolvedRef.Range(
                    ref(cx, from),
                    ref(cx, to)
                  )
              }
          }
        }

      def fromGlobal: Option[Seq[ResolvedRef]] =
        poses.get(Pos.Key.Id(id)).map { case Pos.Value.XYPos(x, y) =>
          Seq(ResolvedRef.One(ref(x, y)))
        }

      fromYAxis.orElse(fromXAxis).orElse(fromGlobal) match {
        case Some(resolved) => resolved.map(_.show).mkString(", ").asRight
        case None => ErrorMsg.idNotFound(id).asLeft
      }
    }

    val placeholders = "%s".r.findAllMatchIn(formula.v).length
    if (formula.ids.length != placeholders) {
      ErrorMsg.wrongFormulaArgs.asLeft
    } else {
      val (leftsSeq, rightsSeq) = formula.ids.map(resolveRef).partition(_.isLeft)

      val lefts = leftsSeq.collect { case Left(value) => value }
      val rights = rightsSeq.collect { case Right(value) => value }

      if (lefts.nonEmpty) Left(lefts.mkString(", "))
      else {
        Right(rights.foldLeft(formula.v) { (acc, value) =>
          acc.replaceFirst("%s", value)
        })
      }
    }
  }
}
