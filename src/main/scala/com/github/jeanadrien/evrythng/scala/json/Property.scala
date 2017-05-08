package com.github.jeanadrien.evrythng.scala.json

import spray.json.JsValue


case class Property (
	value : JsValue,
    key : Option[String] = None,
    timestamp : Option[Long] = None
) {

}
