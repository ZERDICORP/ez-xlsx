package com.nanikin.ezxlsx.apachepoi

import com.nanikin.ezxlsx.Style
import org.apache.poi.ss.usermodel.BorderStyle
import org.apache.poi.ss.usermodel.FillPatternType
import org.apache.poi.ss.usermodel.HorizontalAlignment
import org.apache.poi.ss.usermodel.VerticalAlignment
import org.apache.poi.xssf.usermodel.XSSFCellStyle
import org.apache.poi.xssf.usermodel.XSSFColor
import org.apache.poi.xssf.usermodel.XSSFFont
import org.apache.poi.xssf.usermodel.extensions.XSSFCellBorder.BorderSide

import java.awt.Color

private[apachepoi] object ApacheStyle {

  def apply(
      cellStyle: XSSFCellStyle,
      font: XSSFFont,
      styles: Seq[Style]
  ): Unit =
    styles.foreach {
      case Style.Indent(value) =>
        cellStyle.setIndention(value.toShort)

      case Style.FontSize(value) =>
        font.setFontHeightInPoints(value.toShort)

      case Style.TextWrap =>
        cellStyle.setWrapText(true)

      case Style.TextBold =>
        font.setBold(true)
        cellStyle.setFont(font)

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
        cellStyle.setFont(font)

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
