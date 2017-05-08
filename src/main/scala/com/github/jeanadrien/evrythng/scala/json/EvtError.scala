package com.github.jeanadrien.evrythng.scala.json

/**
  *
  */
case class EvtError(
    status   : Int,
    errors   : List[String],
    code     : Option[Long],
    moreInfo : Option[String]
) {

    def message = s"${status} ${errors.mkString(", ")}"
}
