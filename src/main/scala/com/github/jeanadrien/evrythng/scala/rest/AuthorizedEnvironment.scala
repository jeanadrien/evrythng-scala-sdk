package com.github.jeanadrien.evrythng.scala.rest

/**
  *
  */
trait AuthorizedEnvironment extends Environment {

    def apiKey : String

    override def authorization : Option[String] = Some(apiKey)

}
