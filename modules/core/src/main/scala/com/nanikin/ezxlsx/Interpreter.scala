package com.nanikin.ezxlsx

import com.nanikin.ezxlsx.prep.PrepSheet

private[ezxlsx] trait Interpreter[A] {
  def interpret(sheets: Seq[PrepSheet]): Interpretation[A]
}

private[ezxlsx] object Interpreter {
  def apply[A](implicit a: Interpreter[A]): Interpreter[A] = a
}
