package com.nanikin.ezxlsx.apachepoi

import com.nanikin.ezxlsx._
import com.nanikin.ezxlsx.prep.PrepCell
import com.nanikin.ezxlsx.prep.PrepRow
import com.nanikin.ezxlsx.prep.PrepSheet
import org.apache.poi.ss.util.CellRangeAddress
import org.apache.poi.xssf.usermodel._

private[apachepoi] object ApacheInterpreter {

  def interpret(sheets: Seq[PrepSheet]): ApacheInterpretation = {
    val wb = new XSSFWorkbook()

    def applyStyles(xCell: XSSFCell, value: Value, classes: Seq[String])(implicit
        stylesTable: Map[String, Class]
    ): Unit = {
      val cellStyle = wb.createCellStyle()
      val font = wb.createFont()

      classes.flatMap(stylesTable.get).foreach {
        case Class.Static(_, styles) => ApacheStyle.apply(cellStyle, font, styles)
        case Class.Dependent(_, styles) => ApacheStyle.apply(cellStyle, font, styles(value))
        case Class.Raw(_, styles) => styles(cellStyle, value)
      }

      xCell.setCellStyle(cellStyle)
    }

    def addCells(xSheet: XSSFSheet, xRow: XSSFRow, cells: Seq[PrepCell])(implicit
        stylesTable: Map[String, Class],
        poses: PosMap
    ): Unit =
      cells.zipWithIndex.foreach { case (cell, x) =>
        val xCell: XSSFCell = xRow.createCell(x)
        cell.value.foreach { value =>
          value match {
            case Value.StrVal(v) => xCell.setCellValue(v)
            case Value.IntVal(v) => xCell.setCellValue(v)
            case f: Value.Formula =>
              ApacheFormula.resolve(f, cell.xy, poses) match {
                case Left(error) => xCell.setCellValue(error)
                case Right(formula) => xCell.setCellFormula(formula)
              }
          }
          applyStyles(xCell, value, cell.classes)
        }
        if (cell.settings.autoFilter) {
          xSheet.setAutoFilter(
            new CellRangeAddress(xRow.getRowNum, xRow.getRowNum, xCell.getColumnIndex, xCell.getColumnIndex)
          )
        }
      }

    def addRows(xSheet: XSSFSheet, rows: Seq[PrepRow], start: Int)(implicit
        stylesTable: Map[String, Class],
        poses: PosMap
    ): Int =
      rows.foldLeft(start) { case (y, row) =>
        val xRow = xSheet.createRow(y)
        addCells(xSheet, xRow, row.cells)

        xRow.setHeightInPoints(row.settings.height)

        val nestSize = addRows(xSheet, row.nested, start = y + 1) - y
        if (nestSize > 1) {
          xSheet.groupRow(y + 1, y + nestSize - 1)
          xSheet.setRowGroupCollapsed(y + 1, true)
        }

        y + nestSize
      }

    sheets.foreach { sheet: PrepSheet =>
      implicit val stylesTable: Map[String, Class] = sheet.styles.map(x => x.className -> x).toMap
      implicit val poses: PosMap = sheet.poses

      val xSheet = wb.createSheet(sheet.name)
      addRows(xSheet, sheet.rows, start = 0)
      xSheet.setRowSumsBelow(false)

      sheet.colsWidth.map { case (key, width) =>
        xSheet.setColumnWidth(key, width * 256)
      }
    }

    ApacheInterpretation(wb)
  }
}
