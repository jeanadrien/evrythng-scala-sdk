package com.github.jeanadrien.evrythng.scala.rest

import spray.json.JsonReader

/**
  *
  */
class Page[T](
    val items       : Seq[T],
    val nextPageUrl : Option[String],
    val reader      : JsonReader[T]
) {
    // TODO case class ?
}
