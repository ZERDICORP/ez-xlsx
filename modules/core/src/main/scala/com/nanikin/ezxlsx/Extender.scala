package com.nanikin.ezxlsx

import cats.implicits.catsSyntaxOptionId
import cats.implicits.none
import com.nanikin.ezxlsx.prep.PrepCell
import com.nanikin.ezxlsx.prep.PrepRow
import com.nanikin.ezxlsx.prep.PrepSheet

import scala.collection.mutable

trait Extender {
  def row(sheetId: String, rowId: String, data: Seq[Data]): Extender

  def interpret[A: Interpreter](): Interpretation[A]

  private[ezxlsx] def prepare(): Seq[PrepSheet]
}

object Extender {

  private final class Impl(sheets: Seq[PrepSheet]) extends Extender {

    def row(sheetId: String, rowId: String, data: Seq[Data]): Extender = {
      def generate(row: PrepRow, data: Seq[Data]): Seq[PrepRow] =
        data.map { d =>
          val (cells, _) = row.cells.foldLeft((Seq.empty[PrepCell], 0)) {
            case ((acc, di), cell: PrepCell) if cell.value.nonEmpty =>
              (acc :+ cell.copy(inOneCopy = data.size == 1), di)
            case ((acc, di), cell: PrepCell) if di < d.args.size =>
              val generated = d.args(di) match {
                case Arg.Default(v) =>
                  Seq(
                    cell.copy(
                      value = v.some,
                      inOneCopy = data.size == 1
                    )
                  )
                case Arg.Many(v) =>
                  v.map(x =>
                    cell.copy(
                      value = x.some,
                      inOneCopy = data.size == 1 && v.size == 1
                    )
                  )
              }
              (acc ++ generated, di + 1)
            case ((acc, di), cell: PrepCell) =>
              val withError = cell.copy(
                value = Value.StrVal(ErrorMsg.noCellArg).some,
                inOneCopy = data.size == 1
              )
              (acc :+ withError, di)
          }
          val nested = row.nested.flatten(x => generate(x, d.nested))
          row.copy(
            id = none,
            cells = cells,
            nested = nested
          )
        }

      def extend(rows: Seq[PrepRow]): Seq[PrepRow] =
        rows.flatMap {
          case row: PrepRow if row.id.contains(rowId) => generate(row, data)
          case row: PrepRow => Seq(row)
        }

      val extendedSheets = sheets.map {
        case sheet: PrepSheet if sheet.id.contains(sheetId) => sheet.copy(rows = extend(sheet.rows))
        case other => other
      }
      new Impl(extendedSheets)
    }

    override private[ezxlsx] def prepare(): Seq[PrepSheet] = {
      def addPosId(key: String, value: (Int, Int))(implicit poses: PosMapMutable): Unit =
        poses += (Pos.Key.Id(key) -> Pos.Value.XYPos(x = value._1, y = value._2))

      def addPosMap(key: Pos.Key.XY, id: String, value: Int)(implicit poses: PosMapMutable): Unit =
        poses.get(key) match {
          case Some(Pos.Value.XYMutableMap(map)) =>
            map.get(id) match {
              case Some(poses) => map += (id -> (poses :+ value))
              case None => map += (id -> Seq(value))
            }
          case None =>
            poses += (key -> Pos.Value.XYMutableMap(mutable.HashMap(id -> Seq(value))))
        }

      def withXY(sheets: Seq[PrepSheet]): Seq[PrepSheet] = {
        def _rows(rows: Seq[PrepRow], yN: Int = 0)(implicit poses: PosMapMutable): Seq[PrepRow] = {
          rows.zipWithIndex.flatMap {
            case (row: PrepRow, _) if row.id.nonEmpty => None
            case (row: PrepRow, y) =>
              val cells = row.cells.zipWithIndex.map { case (cell, x) =>
                val pos = (x, y + yN)
                cell.id.foreach { id =>
                  if (cell.inOneCopy) addPosId(id, pos)
                  else {
                    addPosMap(Pos.Key.X(pos._1), id, pos._2)
                    addPosMap(Pos.Key.Y(pos._2), id, pos._1)
                  }
                }
                cell.copy(
                  xy = pos,
                  value = if (cell.value.isEmpty) Value.StrVal(ErrorMsg.noCellArg).some else cell.value
                )
              }
              row
                .copy(
                  cells = cells,
                  nested = _rows(row.nested, y + yN + 1)
                )
                .some
          }
        }
        sheets.map { sheet =>
          implicit val mutablePoses: PosMapMutable = mutable.HashMap.empty
          val newRows = _rows(sheet.rows)
          val immPoses = mutablePoses.map {
            case (pos, innerMap: Pos.Value.XYMutableMap) =>
              pos -> Pos.Value.XYMap(innerMap.v.map { case (k, v) => k -> v.toVector }.toMap)
            case (pos, other: Pos.Value.XYPos) => pos -> other
          }.toMap
          sheet.copy(
            rows = newRows,
            poses = immPoses
          )
        }
      }

      withXY(sheets)
    }

    def interpret[A: Interpreter](): Interpretation[A] =
      Interpreter[A].interpret(prepare())
  }

  private def prep(sheets: Seq[Sheet]): Seq[PrepSheet] = {
    def prepCells(cells: Seq[Cell]): Seq[PrepCell] =
      cells.map {
        case Cell.Default(value, id, classes, settings) =>
          PrepCell(
            id = id,
            value = value.some,
            classes = classes,
            settings = settings,
            xy = (0, 0),
            inOneCopy = true
          )
        case Cell.Arg(id, classes, settings) =>
          PrepCell(
            id = id,
            value = none,
            classes = classes,
            settings = settings,
            xy = (0, 0),
            inOneCopy = true
          )
      }
    def prepRows(rows: Seq[Row]): Seq[PrepRow] = {
      rows.map {
        case Row.Default(cells, id, nested, settings) =>
          PrepRow(
            id = id,
            cells = prepCells(cells),
            nested = prepRows(Seq(nested).flatten),
            settings = settings
          )
        case Row.Nested(cells, nested, settings) =>
          PrepRow(
            id = none,
            cells = prepCells(cells),
            nested = prepRows(Seq(nested).flatten),
            settings = settings
          )
      }
    }
    sheets.map { sheet: Sheet =>
      PrepSheet(
        name = sheet.name,
        id = sheet.id,
        colsWidth = sheet.colsWidth,
        rows = prepRows(sheet.rows),
        poses = Map.empty,
        styles = sheet.styles
      )
    }
  }

  def apply(template: Template): Extender =
    new Impl(prep(template.sheets))
}
