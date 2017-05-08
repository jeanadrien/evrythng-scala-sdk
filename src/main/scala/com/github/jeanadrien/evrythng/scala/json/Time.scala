package com.github.jeanadrien.evrythng.scala.json

/**
  * {
  *   "timestamp": Timestamp,
  *   "localTime": String,
  *   "nextChange": Long,
  *   "offset": Long
  * }
  */
case class Time(
    timestamp  : Long,
    localTime  : Option[String],
    nextChange : Option[Long],
    offset     : Option[Long]
)
