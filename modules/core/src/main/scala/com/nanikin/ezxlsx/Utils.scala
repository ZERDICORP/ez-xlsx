package com.nanikin.ezxlsx

private[ezxlsx] object Utils {

  def jerkily(seq: Seq[Int]): Seq[Seq[Int]] = {
    if (seq.isEmpty) return Seq.empty

    seq.tail
      .foldLeft(Seq(Seq(seq.head))) { case (acc, curr) =>
        val lastGroup = acc.last
        if (curr == lastGroup.last + 1) {
          acc.init :+ (lastGroup :+ curr)
        } else {
          acc :+ Seq(curr)
        }
      }
      .map {
        case Seq(single) => Seq(single)
        case group => Seq(group.head, group.last)
      }
  }
}
