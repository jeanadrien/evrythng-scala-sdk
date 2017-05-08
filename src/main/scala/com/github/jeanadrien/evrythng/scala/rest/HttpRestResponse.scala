package com.github.jeanadrien.evrythng.scala.rest

/**
  *
  */
trait HttpRestResponse {

    def body : String = new String(bodyAsBytes, "UTF-8")

    def bodyAsBytes : Array[Byte]

    def header(name : String) : Option[String]

    def status : Int

    def statusText : String

}
