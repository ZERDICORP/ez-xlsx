package com.nanikin.ezxlsx

trait Template {
  private[ezxlsx] val sheets: Seq[Sheet]

  def extend(): Extender
}

object Template {

  private final class XlsxTemplateImpl(val sheets: Seq[Sheet]) extends Template {
    def extend(): Extender = Extender(this)
  }

  private[ezxlsx] trait XlsxTemplateStep {
    def sheet(_sheet: Sheet): XlsxTemplateStep
    def fixate(): Template
  }

  private final class XlsxTemplateStepImpl(sheets: Seq[Sheet]) extends XlsxTemplateStep {

    def sheet(_sheet: Sheet): XlsxTemplateStep =
      new XlsxTemplateStepImpl(sheets :+ _sheet)

    def fixate(): Template = new XlsxTemplateImpl(sheets)
  }

  def sheet(_sheet: Sheet): XlsxTemplateStep =
    new XlsxTemplateStepImpl(Seq(_sheet))
}
