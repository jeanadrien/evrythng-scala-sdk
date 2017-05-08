package com.github.jeanadrien.evrythng.scala.rest

import spray.json.JsonFormat

/**
  *
  */
abstract class ResourceContext[R](val env : Environment, val urlPath : String)(implicit val format : JsonFormat[R]) {

}
