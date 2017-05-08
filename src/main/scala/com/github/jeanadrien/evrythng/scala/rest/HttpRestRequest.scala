package com.github.jeanadrien.evrythng.scala.rest

import scala.concurrent.Future

/**
  *
  */
trait HttpRestRequest {

    def get() : Future[HttpRestResponse]

    def post(body : String) : Future[HttpRestResponse]

    def put(body : String) : Future[HttpRestResponse]

    def delete() : Future[HttpRestResponse]

}
