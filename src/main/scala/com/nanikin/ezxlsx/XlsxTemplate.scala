package com.nanikin.ezxlsx

import cats.implicits.catsSyntaxOptionId
import cats.implicits.none
import com.nanikin.ezxlsx.model.Row
import com.nanikin.ezxlsx.model.Sheet

trait XlsxTemplate {
  def cast(): XlsxCast
}

object XlsxTemplate {

  private final class XlsxTemplateImpl(sheets: Seq[Sheet]) extends XlsxTemplate {
    def cast(): XlsxCast = XlsxCast(sheets)
  }

  trait XlsxTemplateStep {
    def sheet(name: String, columnWidth: Seq[Int])(rows: Row*): XlsxTemplateStep
    def sheet(name: String, id: String, columnWidth: Seq[Int])(rows: Row*): XlsxTemplateStep
    def fix(): XlsxTemplate
  }

  private final class XlsxTemplateStepImpl(sheets: Seq[Sheet]) extends XlsxTemplateStep {

    def sheet(name: String, columnWidth: Seq[Int])(rows: Row*): XlsxTemplateStep =
      new XlsxTemplateStepImpl(sheets :+ Sheet(name, id = none, rows, columnWidth))

    def sheet(name: String, id: String, columnWidth: Seq[Int])(rows: Row*): XlsxTemplateStep =
      new XlsxTemplateStepImpl(sheets :+ Sheet(name, id.some, rows, columnWidth))

    def fix(): XlsxTemplate = new XlsxTemplateImpl(sheets)
  }

  def sheet(name: String, columnWidth: Seq[Int])(rows: Row*): XlsxTemplateStep =
    new XlsxTemplateStepImpl(Seq(Sheet(name, id = none, rows, columnWidth)))

  def sheet(name: String, id: String, columnWidth: Seq[Int])(rows: Row*): XlsxTemplateStep =
    new XlsxTemplateStepImpl(Seq(Sheet(name, id.some, rows, columnWidth)))
}
