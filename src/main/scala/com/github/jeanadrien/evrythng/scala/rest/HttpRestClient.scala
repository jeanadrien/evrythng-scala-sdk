package com.github.jeanadrien.evrythng.scala.rest

/**
  *
  */
trait HttpRestClient {

    def buildRequest(
        url : String,
        headers : Seq[(String, String)],
        queryParameters : Seq[(String, String)]
    ) : HttpRestRequest

    def shutDown : Unit

}
