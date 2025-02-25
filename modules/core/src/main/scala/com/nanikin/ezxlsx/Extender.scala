package com.nanikin.ezxlsx

import cats.implicits.catsSyntaxOptionId
import cats.implicits.none
import com.nanikin.ezxlsx.prep.PrepCell
import com.nanikin.ezxlsx.prep.PrepRow
import com.nanikin.ezxlsx.prep.PrepSheet
import com.nanikin.ezxlsx.prep.realRowsLen

import scala.collection.mutable

trait Extender {
  def row(sheetId: String, rowId: String, data: Seq[Data]): Extender
  def repeatCol(sheetId: String, colId: String, times: Int): Extender

  def interpret[A: Interpreter](): Interpretation[A]

  private[ezxlsx] def prepare(): Seq[PrepSheet]
}

object Extender {

  private final class Impl(sheets: Seq[PrepSheet]) extends Extender {

    def row(sheetId: String, rowId: String, data: Seq[Data]): Extender = {
      def realDataLen(data: Seq[Data]): Int =
        data.size + data.map(d => realDataLen(d.nested)).sum

      def generate(row: PrepRow, data: Seq[Data], dataLen: Int): Seq[PrepRow] =
        data.map { d =>
          val (cells, _) = row.cells.foldLeft((Seq.empty[PrepCell], 0)) {
            case ((acc, di), cell: PrepCell) if cell.value.nonEmpty =>
              (acc :+ cell.copy(inOneCopy = dataLen == 1), di)
            case ((acc, di), cell: PrepCell) if di < d.args.size =>
              val generated = d.args(di) match {
                case Arg.Default(v, classes) =>
                  Seq(
                    cell.copy(
                      value = v.some,
                      classes = cell.classes ++ classes,
                      inOneCopy = dataLen == 1
                    )
                  )
                case Arg.Many(v, classes) =>
                  v.map(x =>
                    cell.copy(
                      value = x.some,
                      classes = cell.classes ++ classes,
                      inOneCopy = dataLen == 1 && v.size == 1
                    )
                  )
              }
              (acc ++ generated, di + 1)
            case ((acc, di), cell: PrepCell) =>
              val withError = cell.copy(
                value = Value.StrVal(ErrorMsg.noCellArg).some,
                inOneCopy = dataLen == 1
              )
              (acc :+ withError, di)
          }
          val nested = row.nested.flatten(x => generate(x, d.nested, dataLen))
          row.copy(
            id = none,
            cells = cells,
            nested = nested,
            classes = row.classes ++ d.classes
          )
        }

      def extend(rows: Seq[PrepRow]): Seq[PrepRow] =
        rows.flatMap {
          case row: PrepRow if row.id.contains(rowId) => generate(row, data, realDataLen(data))
          case row: PrepRow => Seq(row)
        }

      val extendedSheets = sheets.map {
        case sheet: PrepSheet if sheet.id.contains(sheetId) => sheet.copy(rows = extend(sheet.rows))
        case other => other
      }
      new Impl(extendedSheets)
    }

    override def repeatCol(sheetId: String, colId: String, times: Int): Extender = {
      val extendedSheets = sheets.map {
        case sheet: PrepSheet if sheet.id.contains(sheetId) =>
          val cols = sheet.cols.map {
            case col: Col.Default if col.id.contains(colId) => col.copy(repeats = times)
            case other => other
          }
          sheet.copy(cols = cols)
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
          case _ => ()
        }

      def withXY(sheets: Seq[PrepSheet]): Seq[PrepSheet] = {
        def _rows(rows: Seq[PrepRow], yN: Int = 0)(implicit poses: PosMapMutable): Seq[PrepRow] = {
          val (prepRows, _) = rows.foldLeft((Seq.empty[PrepRow], yN)) {
            case ((acc, y), row: PrepRow) if row.id.nonEmpty => (acc, y)
            case ((acc, y), row: PrepRow) =>
              val cells = row.cells.zipWithIndex.map { case (cell, x) =>
                cell.id.foreach { id =>
                  if (cell.inOneCopy) addPosId(id, (x, y))
                  else {
                    addPosMap(Pos.Key.X(x), id, y)
                    addPosMap(Pos.Key.Y(y), id, x)
                  }
                }
                cell.copy(
                  xy = (x, y),
                  value = if (cell.value.isEmpty) Value.StrVal(ErrorMsg.noCellArg).some else cell.value
                )
              }
              val nest: Seq[PrepRow] = _rows(row.nested, y + 1)
              (
                acc :+ row.copy(
                  cells = cells,
                  nested = nest
                ),
                y + realRowsLen(nest) + 1
              )
          }
          prepRows
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
            inOneCopy = true,
            mapper = none
          )
        case Cell.Arg(id, classes, settings, mapper) =>
          PrepCell(
            id = id,
            value = none,
            classes = classes,
            settings = settings,
            xy = (0, 0),
            inOneCopy = true,
            mapper = mapper
          )
      }
    def prepRows(rows: Seq[Row]): Seq[PrepRow] = {
      rows.map {
        case Row.Default(cells, id, nested, classes, settings) =>
          PrepRow(
            id = id,
            cells = prepCells(cells),
            nested = prepRows(Seq(nested).flatten),
            classes = classes,
            settings = settings
          )
        case Row.Nested(cells, nested, classes, settings) =>
          PrepRow(
            id = none,
            cells = prepCells(cells),
            nested = prepRows(Seq(nested).flatten),
            classes = classes,
            settings = settings
          )
      }
    }
    sheets.map { sheet: Sheet =>
      PrepSheet(
        name = sheet.name,
        id = sheet.id,
        cols = sheet.cols,
        colsInFreeze = sheet.colsInFreeze,
        rowsInFreeze = sheet.rowsInFreeze,
        rows = prepRows(sheet.rows),
        poses = Map.empty,
        styles = sheet.styles
      )
    }
  }

  def apply(template: Template): Extender =
    new Impl(prep(template.sheets))
}
