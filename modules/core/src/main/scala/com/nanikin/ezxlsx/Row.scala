package com.nanikin.ezxlsx

import cats.implicits.catsSyntaxOptionId

private[ezxlsx] sealed trait Row

object Row {

  private[ezxlsx] final case class Default(
      cells: Seq[Cell],
      id: Option[String] = None,
      nested: Option[Row.Nested] = None,
      settings: Settings = Settings()
  ) extends Row

  def apply(cells: Cell*): Default = Default(cells)
  def empty: Default = Default(Seq.empty)

  private[ezxlsx] final case class Nested(
      cells: Seq[Cell],
      nested: Option[Row.Nested] = None,
      settings: Settings = Settings()
  ) extends Row

  def nested(cells: Cell*): Nested = Nested(cells)
  def nested: Nested = Nested(Seq.empty)

  implicit class DefaultRowOps(row: Default) {
    def withId(id: String): Default = row.copy(id = id.some)
    def withNested(nested: Nested): Default = row.copy(nested = nested.some)

    def withSettings(
        height: Int = Settings.DEFAULT_HEIGHT
    ): Default = row.copy(settings = Settings(height))
  }

  implicit class NestedRowOps(row: Nested) {
    def withNested(nested: Nested): Nested = row.copy(nested = nested.some)

    def withSettings(
        height: Int = Settings.DEFAULT_HEIGHT
    ): Nested = row.copy(settings = Settings(height))
  }

  final case class Settings(
      height: Int = Settings.DEFAULT_HEIGHT
  )

  object Settings {
    val DEFAULT_HEIGHT: Int = 15
  }
}
