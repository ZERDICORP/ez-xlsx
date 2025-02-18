package com.nanikin.ezxlsx

import org.scalatest.funsuite.AnyFunSuite

class UtilsTest extends AnyFunSuite {

  test("jerkily works perfectly") {
    val data = Seq(
      1, 2, 3, 4, 5, 6, 8, 9, 15, 25, 30, 45, 46, 47
    )

    val result = Utils.jerkily(data)

    assert(
      result == Seq(
        Seq(1, 6),
        Seq(8, 9),
        Seq(15),
        Seq(25),
        Seq(30),
        Seq(45, 47)
      )
    )
  }
}
