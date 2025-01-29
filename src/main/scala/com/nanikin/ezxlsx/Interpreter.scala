package com.nanikin.ezxlsx

import com.nanikin.ezxlsx.model.CellStyle.Align
import com.nanikin.ezxlsx.model.Cell
import com.nanikin.ezxlsx.model.CellStyle
import com.nanikin.ezxlsx.model.Row
import com.nanikin.ezxlsx.model.Sheet
import org.apache.poi.ss.usermodel.BorderStyle
import org.apache.poi.ss.usermodel.FillPatternType
import org.apache.poi.ss.usermodel.HorizontalAlignment
import org.apache.poi.ss.usermodel.VerticalAlignment
import org.apache.poi.ss.util.CellRangeAddress
import org.apache.poi.xssf.usermodel._
import org.apache.poi.xssf.usermodel.extensions.XSSFCellBorder.BorderSide

import java.awt.Color

private[ezxlsx] object Interpreter {

  def interpret(sheets: Seq[Sheet]): XSSFWorkbook = {
    val wb = new XSSFWorkbook()

    def interpretVAlign(align: CellStyle.Align.V): VerticalAlignment =
      align match {
        case Align.V.Top => VerticalAlignment.TOP
        case Align.V.Bottom => VerticalAlignment.BOTTOM
        case Align.V.Center => VerticalAlignment.CENTER
      }
    def interpretHAlign(align: CellStyle.Align.H): HorizontalAlignment =
      align match {
        case Align.H.Left => HorizontalAlignment.LEFT
        case Align.H.Right => HorizontalAlignment.RIGHT
        case Align.H.Center => HorizontalAlignment.CENTER
      }

    def applyStyles(xCell: XSSFCell, styles: Seq[CellStyle]): Unit = {
      val cellStyle = wb.createCellStyle()
      val font = wb.createFont()

      styles.foreach {
        case CellStyle.Indent(value) =>
          cellStyle.setIndention(value.toShort)

        case CellStyle.FontSize(value) =>
          font.setFontHeightInPoints(value.toShort)

        case CellStyle.TextWrap =>
          cellStyle.setWrapText(true)

        case CellStyle.TextBold =>
          font.setBold(true)
          cellStyle.setFont(font)

        case CellStyle.BgColorHex(hex) =>
          val color = Color.decode(hex)
          val xssfColor = new XSSFColor(color, null)
          cellStyle.setFillForegroundColor(xssfColor)
          cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND)

          val xssfBorderColor = new XSSFColor(Color.decode("#c9c7c7"), null)

          cellStyle.setBorderBottom(BorderStyle.THIN)
          cellStyle.setBorderTop(BorderStyle.THIN)
          cellStyle.setBorderLeft(BorderStyle.THIN)
          cellStyle.setBorderRight(BorderStyle.THIN)

          cellStyle.setBorderColor(BorderSide.TOP, xssfBorderColor)
          cellStyle.setBorderColor(BorderSide.BOTTOM, xssfBorderColor)
          cellStyle.setBorderColor(BorderSide.LEFT, xssfBorderColor)
          cellStyle.setBorderColor(BorderSide.RIGHT, xssfBorderColor)

        case CellStyle.TextColorHex(hex) =>
          val color = Color.decode(hex)
          val xssfColor = new XSSFColor(color, null)
          font.setColor(xssfColor)
          cellStyle.setFont(font)

        case align: CellStyle.Align =>
          align match {
            case v: Align.V =>
              cellStyle.setVerticalAlignment(interpretVAlign(v))
            case h: Align.H =>
              cellStyle.setAlignment(interpretHAlign(h))
          }
      }

      xCell.setCellStyle(cellStyle)
    }

    def addCells(xSheet: XSSFSheet, xRow: XSSFRow, cells: Seq[Cell]): Unit = {
      cells.zipWithIndex.foreach {
        case (cell: Cell.Norm, x) =>
          val xCell: XSSFCell = xRow.createCell(x)
          cell.value match {
            case v: Int => xCell.setCellValue(v)
            case v: String => xCell.setCellValue(v)
          }
          if (cell.settings.autoFilter) {
            xSheet.setAutoFilter(
              new CellRangeAddress(xRow.getRowNum, xRow.getRowNum, xCell.getColumnIndex, xCell.getColumnIndex)
            )
          }
          applyStyles(xCell, cell.styles)
        case (_: Cell.Gen, _) => throw InterpretException(s"Found 'Gen' cell while interpret")
      }
    }

    def addRows(xSheet: XSSFSheet, rows: Seq[Row], start: Int): Int =
      rows.foldLeft(start) {
        case (y, row: Row.Norm) =>
          val xRow = xSheet.createRow(y)
          addCells(xSheet, xRow, row.cells)

          xRow.setHeightInPoints(row.settings.height)

          val nestSize = addRows(xSheet, row.nest, start = y + 1) - y
          if (nestSize > 1) {
            xSheet.groupRow(y + 1, y + nestSize - 1)
            xSheet.setRowGroupCollapsed(y + 1, true)
          }

          y + nestSize
        case (_, _: Row.GenBase) => throw InterpretException(s"Found 'Gen' row while interpret")
      }

    sheets.foreach { sheet =>
      val xSheet = wb.createSheet(sheet.name)
      addRows(xSheet, sheet.rows, start = 0)
      xSheet.setRowSumsBelow(false)

      sheet.columnWidth.zipWithIndex.map { case (w, i) =>
        xSheet.setColumnWidth(i, w * 256)
      }
    }

    wb
  }
}
