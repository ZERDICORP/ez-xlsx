package com.nanikin.ezxlsx

import cats.implicits.catsSyntaxOptionId

sealed trait Cell

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
      settings: Settings = Settings(),
      mapper: Option[ValueMapper] = None
  ) extends Cell

  def arg: Arg = Arg()

  implicit class DefaultCellOps(cell: Default) {
    def withId(id: String): Default = cell.copy(id = id.some)
    def withClasses(classes: String*): Default = cell.copy(classes = classes)

    def withSettings(
        autoFilter: Boolean = false,
        merge: Boolean = false
    ): Default = cell.copy(settings = Settings(autoFilter, merge))
  }

  implicit class ArgCellOps(cell: Arg) {
    def withId(id: String): Arg = cell.copy(id = id.some)
    def withClasses(classes: String*): Arg = cell.copy(classes = classes)
    def withMapper(mapper: ValueMapper): Arg = cell.copy(mapper = mapper.some)

    def withSettings(
        autoFilter: Boolean = false,
        merge: Boolean = false
    ): Arg = cell.copy(settings = Settings(autoFilter, merge))
  }

  final case class Settings(
      autoFilter: Boolean = false,
      merge: Boolean = false
  )
}
