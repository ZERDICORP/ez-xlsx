package com.nanikin.ezxlsx.apachepoi

import cats.implicits.catsSyntaxOptionId
import com.nanikin.ezxlsx._
import com.nanikin.ezxlsx.prep.PrepCell
import com.nanikin.ezxlsx.prep.PrepRow
import com.nanikin.ezxlsx.prep.PrepSheet
import org.apache.poi.ss.util.CellRangeAddress
import org.apache.poi.xssf.usermodel._

private[apachepoi] object ApacheInterpreter {

  def interpret(sheets: Seq[PrepSheet]): ApacheInterpretation = {
    val wb = new XSSFWorkbook()

    def applyStyles(xCell: XSSFCell, xSheet: XSSFSheet, value: Value, xy: (Int, Int), classes: Seq[String])(implicit
        stylesTable: Map[String, Class]
    ): Unit = {
      val cellStyle: XSSFCellStyle = wb.createCellStyle()
      val font = wb.createFont()

      val cellRef = ApacheFormula.ref(xy)

      classes.flatMap(stylesTable.get).foreach {
        case Class.Static(_, styles) => ApacheStyle.apply(wb, xSheet, cellStyle, font, cellRef, styles)
        case Class.ValDependent(_, styles) => ApacheStyle.apply(wb, xSheet, cellStyle, font, cellRef, styles(value))
        case Class.PosDependent(_, styles) =>
          ApacheStyle.apply(wb, xSheet, cellStyle, font, cellRef, styles(value, xy))
        case Class.Raw(_, styles) => styles(cellStyle, value)
      }

      cellStyle.setFont(font)
      xCell.setCellStyle(cellStyle)
    }

    def addCells(xSheet: XSSFSheet, xRow: XSSFRow, cells: Seq[PrepCell], commonClasses: Seq[String])(implicit
        stylesTable: Map[String, Class],
        poses: PosMap
    ): Unit =
      cells.zipWithIndex.foldLeft(Option.empty[Int]) { case (agg, (cell, x)) =>
        val xCell: XSSFCell = xRow.createCell(x)
        cell.value.foreach { value =>
          applyStyles(xCell, xSheet, value, cell.xy, commonClasses ++ cell.classes)
          value match {
            case Value.StrVal(v) => xCell.setCellValue(v)
            case Value.IntVal(v) => xCell.setCellValue(v)
            case Value.DblVal(v) => xCell.setCellValue(v)
            case f: Value.Formula =>
              ApacheFormula.resolve(f, cell.xy, poses) match {
                case Left(error) => xCell.setCellValue(error)
                case Right(formula) => xCell.setCellFormula(formula)
              }
          }
        }
        (cell.settings.autoFilter, agg) match {
          case (true, Some(x)) =>
            xSheet.setAutoFilter(
              new CellRangeAddress(xRow.getRowNum, xRow.getRowNum, x, xCell.getColumnIndex)
            )
            x.some
          case (true, None) =>
            xSheet.setAutoFilter(
              new CellRangeAddress(xRow.getRowNum, xRow.getRowNum, xCell.getColumnIndex, xCell.getColumnIndex)
            )
            xCell.getColumnIndex.some
          case (false, _) => agg
        }
      }

    def addRows(xSheet: XSSFSheet, rows: Seq[PrepRow], start: Int)(implicit
        stylesTable: Map[String, Class],
        poses: PosMap
    ): Int =
      rows.foldLeft(start) { case (y, row) =>
        val xRow = xSheet.createRow(y)
        addCells(xSheet, xRow, row.cells, row.classes)

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
      xSheet.createFreezePane(sheet.colsInFreeze, sheet.rowsInFreeze)

      sheet.colsWidth.map { case (key, width) =>
        xSheet.setColumnWidth(key, width * 256)
      }
    }

    ApacheInterpretation(wb)
  }
}
