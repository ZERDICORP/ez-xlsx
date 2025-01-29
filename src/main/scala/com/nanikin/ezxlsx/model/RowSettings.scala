package com.nanikin.ezxlsx.model

final case class RowSettings(
    height: Int = RowSettings.DEFAULT_HEIGHT
)

object RowSettings {

  val DEFAULT_HEIGHT: Int = 15
}
