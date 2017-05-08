package com.github.jeanadrien.evrythng.scala.json

/**
  *
  */
case class Location(
    latitude : Option[Double],
    longitude : Option[Double],
    timestamp : Option[Long] = None
) {

}

object Location {

    def of(latitude : Double, longitude : Double): Location = Location(Some(latitude), Some(longitude))

}
