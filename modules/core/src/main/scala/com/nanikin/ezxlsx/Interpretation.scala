package com.nanikin.ezxlsx

trait Interpretation[A] {
  val result: A

  def save(filepath: String): Unit
  def asBytes: Array[Byte]
}
