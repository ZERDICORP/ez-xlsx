package com.nanikin.ezxlsx.apachepoi

import com.nanikin.ezxlsx.Interpretation
import org.apache.poi.xssf.usermodel.XSSFWorkbook

import java.io.FileOutputStream
import java.nio.file.Paths

final case class XlsxApacheInterpretation(result: XSSFWorkbook) extends Interpretation[XSSFWorkbook] {

  override def save(filepath: String): Unit = {
    val outputStream = new FileOutputStream(Paths.get(filepath).toFile)
    result.write(outputStream)
    result.close()
    outputStream.close()
  }
}
