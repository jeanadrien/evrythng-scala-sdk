package com.github.jeanadrien.evrythng.scala.json

/**
  *
  */
case class Ref(id : String) {

    require(id.matches("\\w{24}"))

    override def toString = id

}
