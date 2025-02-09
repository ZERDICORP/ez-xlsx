package com.nanikin.ezxlsx

object ErrorMsg {

  private def mk(msg: String): String = {
    val replaced = msg.trim.replace(" ", "-")
    f"<$replaced>"
  }

  val noCellArg: String = mk("no arg")
  val idNotFound: String => String = id => mk(f"cell id '$id' not found")
  val wrongFormulaArgs: String = mk("wrong formula ids amount")
}
