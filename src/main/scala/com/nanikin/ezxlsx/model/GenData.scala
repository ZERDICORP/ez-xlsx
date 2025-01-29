package com.nanikin.ezxlsx.model

final case class GenData(
    args: Seq[Any],
    nest: Seq[GenData] = Seq.empty
)
