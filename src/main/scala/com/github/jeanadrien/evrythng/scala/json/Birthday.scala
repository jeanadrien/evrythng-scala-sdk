package com.github.jeanadrien.evrythng.scala.json

/**
  * {
  *   "day": Integer,
  *   "month": Integer,
  *   "year": Integer
  * }
  */
case class Birthday(
    day : Int,
    month : Int,
    year : Option[Int] = None
)