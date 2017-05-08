package com.github.jeanadrien.evrythng.scala.json

/**
  *
  */
case class Position(x : Double, y : Double)  {

    def toLocation = Location(latitude = Some(y), longitude = Some(x))

    def point = (x, y)

}

object Position {

    def apply(pair : (Double, Double)) : Position = Position(pair._1, pair._2)

}
