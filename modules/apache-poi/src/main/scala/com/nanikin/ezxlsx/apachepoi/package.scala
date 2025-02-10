package com.nanikin.ezxlsx

import com.nanikin.ezxlsx.prep.PrepSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook

package object apachepoi {

  implicit val interpreter: Interpreter[XSSFWorkbook] = new Interpreter[XSSFWorkbook] {

    override def interpret(sheets: Seq[PrepSheet]): Interpretation[XSSFWorkbook] =
      ApacheInterpreter.interpret(sheets)
  }
}
