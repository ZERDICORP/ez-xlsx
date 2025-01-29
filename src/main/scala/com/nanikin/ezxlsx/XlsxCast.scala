package com.nanikin.ezxlsx

import com.nanikin.ezxlsx.model.Row.GenBase
import com.nanikin.ezxlsx.model.Cell
import com.nanikin.ezxlsx.model.GenData
import com.nanikin.ezxlsx.model.Row
import com.nanikin.ezxlsx.model.Sheet

import java.io.FileOutputStream

trait XlsxCast {
  def genFill(sheetId: String, rowId: String, data: Seq[GenData]): XlsxCast
  def save(filename: String): Unit
}

object XlsxCast {

  private final class XlsxCastImpl(sheets: Seq[Sheet]) extends XlsxCast {

    def _genNorm(row: GenBase, data: Seq[GenData]): Seq[Row.Norm] =
      data.map { d =>
        val (normCells, _) = row.cells.foldLeft((Seq.empty[Cell.Norm], 0)) { case ((acc, i), cell) =>
          cell match {
            case n: Cell.Norm => (acc :+ n, i)
            case Cell.Gen(styles, _) =>
              (
                acc :+ Cell.Norm(
                  value = d.args(i),
                  styles = styles(d.args(i))
                ),
                i + 1
              )
          }
        }
        Row.Norm(
          cells = normCells,
          nest = row.nest.map { child =>
            _genNorm(child, d.nest)
          }.getOrElse(Seq.empty),
          settings = row.settings
        )
      }

    def _replaceRow(rows: Seq[Row], rowId: String, data: Seq[GenData]): Seq[Row] =
      rows.flatMap {
        case genMatch @ Row.Gen(id, _, _, _) if id == rowId => _genNorm(genMatch, data)
        case gen: Row.Gen => Seq(gen)
        case norm: Row.Norm =>
          Seq(
            norm.copy(
              nest = _replaceRow(norm.nest, rowId, data)
            )
          )
      }

    def genFill(sheetId: String, rowId: String, data: Seq[GenData]): XlsxCast = {
      val modSheets = sheets.map { s =>
        if (s.id.contains(sheetId))
          s.copy(rows = _replaceRow(s.rows, rowId, data))
        else s
      }
      new XlsxCastImpl(modSheets)
    }

    def save(filename: String): Unit = {
      val wb = Interpreter.interpret(sheets)

      val outputStream = new FileOutputStream(filename)
      wb.write(outputStream)
      wb.close()
      outputStream.close()
    }
  }

  def apply(sheets: Seq[Sheet]): XlsxCast = new XlsxCastImpl(sheets)
}
