package com.nanikin.ezxlsx

import cats.implicits.catsSyntaxOptionId

private[ezxlsx] sealed trait Cell

object Cell {

  private[ezxlsx] final case class Default(
      value: Value,
      id: Option[String] = None,
      classes: Seq[String] = Seq.empty,
      settings: Settings = Settings()
  ) extends Cell

  def apply(value: Value): Default = Default(value)
  def empty: Default = Default("")

  private[ezxlsx] final case class Arg(
      id: Option[String] = None,
      classes: Seq[String] = Seq.empty,
      settings: Settings = Settings()
  ) extends Cell

  def arg: Arg = Arg()

  implicit class DefaultCellOps(cell: Default) {
    def withId(id: String): Default = cell.copy(id = id.some)
    def withClasses(classes: String*): Default = cell.copy(classes = classes)

    def withSettings(
        autoFilter: Boolean = false
    ): Default = cell.copy(settings = Settings(autoFilter))
  }

  implicit class ArgCellOps(cell: Arg) {
    def withId(id: String): Arg = cell.copy(id = id.some)
    def withClasses(classes: String*): Arg = cell.copy(classes = classes)

    def withSettings(
        autoFilter: Boolean = false
    ): Arg = cell.copy(settings = Settings(autoFilter))
  }

  final case class Settings(
      autoFilter: Boolean = false
  )
}
