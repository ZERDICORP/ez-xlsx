package com.nanikin.ezxlsx.apachepoi

import com.nanikin.ezxlsx.Style
import org.apache.poi.ss.usermodel._
import org.apache.poi.ss.util.CellRangeAddress
import org.apache.poi.xssf.usermodel._
import org.apache.poi.xssf.usermodel.extensions.XSSFCellBorder.BorderSide

import java.awt.Color

private[apachepoi] object ApacheStyle {

  private def applyConditional(xSheet: XSSFSheet, cellRef: String, cond: Style.Conditional): Unit =
    cond match {
      case Style.Conditional.BgColorHexByNumber((startN, startHex), (midN, midHex), (endN, endHex)) =>
        val cf = xSheet.getSheetConditionalFormatting
        val cfr = xSheet.getSheetConditionalFormatting.createConditionalFormattingColorScaleRule()

        val csf: XSSFColorScaleFormatting = cfr.createColorScaleFormatting()

        csf.getThresholds()(0).setRangeType(ConditionalFormattingThreshold.RangeType.NUMBER)
        csf.getThresholds()(0).setValue(startN)

        csf.getThresholds()(1).setRangeType(ConditionalFormattingThreshold.RangeType.NUMBER)
        csf.getThresholds()(1).setValue(midN)

        csf.getThresholds()(2).setRangeType(ConditionalFormattingThreshold.RangeType.NUMBER)
        csf.getThresholds()(2).setValue(endN)

        csf.getColors()(0).asInstanceOf[ExtendedColor].setARGBHex(startHex)
        csf.getColors()(1).asInstanceOf[ExtendedColor].setARGBHex(midHex)
        csf.getColors()(2).asInstanceOf[ExtendedColor].setARGBHex(endHex)

        cf.addConditionalFormatting(
          Array(CellRangeAddress.valueOf(cellRef)),
          cfr
        )
    }

  def apply(
      wb: XSSFWorkbook,
      xSheet: XSSFSheet,
      cellStyle: XSSFCellStyle,
      font: XSSFFont,
      cellRef: String,
      styles: Seq[Style]
  ): Unit =
    styles.foreach {
      case cond: Style.Conditional => applyConditional(xSheet, cellRef, cond)
      case Style.DataFormat(value) =>
        val format = wb.createDataFormat()
        cellStyle.setDataFormat(format.getFormat(value))

      case Style.Indent(value) =>
        cellStyle.setIndention(value.toShort)

      case Style.FontFamily(value) =>
        font.setFontName(value)

      case Style.FontSize(value) =>
        font.setFontHeightInPoints(value.toShort)

      case Style.TextWrap =>
        cellStyle.setWrapText(true)

      case Style.TextItalic =>
        font.setItalic(true)

      case Style.TextBold =>
        font.setBold(true)

      case Style.BgColorHex(hex) =>
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

      case Style.TextColorHex(hex) =>
        val color = Color.decode(hex)
        val xssfColor = new XSSFColor(color, null)
        font.setColor(xssfColor)

      case align: Style.Align =>
        align match {
          case v: Style.Align.V =>
            cellStyle.setVerticalAlignment(alignV(v))
          case h: Style.Align.H =>
            cellStyle.setAlignment(alignH(h))
        }
    }

  def alignV(align: Style.Align.V): VerticalAlignment =
    align match {
      case Style.Align.V.Top => VerticalAlignment.TOP
      case Style.Align.V.Bottom => VerticalAlignment.BOTTOM
      case Style.Align.V.Center => VerticalAlignment.CENTER
    }

  def alignH(align: Style.Align.H): HorizontalAlignment =
    align match {
      case Style.Align.H.Left => HorizontalAlignment.LEFT
      case Style.Align.H.Right => HorizontalAlignment.RIGHT
      case Style.Align.H.Center => HorizontalAlignment.CENTER
    }
}
