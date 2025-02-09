package com.nanikin.ezxlsx

import org.apache.poi.xssf.usermodel.XSSFWorkbook

package object apachepoi {

  implicit val interpreter: Interpreter[XSSFWorkbook] = ApacheInterpreter.interpret
}
