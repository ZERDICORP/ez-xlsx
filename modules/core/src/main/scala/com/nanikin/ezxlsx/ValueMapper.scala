package com.nanikin.ezxlsx

final case class ValueMapper(f: Any => Value) {
  def apply(v: Any): Value = f(v)
}
