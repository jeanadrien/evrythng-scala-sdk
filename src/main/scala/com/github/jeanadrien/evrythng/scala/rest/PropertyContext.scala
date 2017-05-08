package com.github.jeanadrien.evrythng.scala.rest

import com.github.jeanadrien.evrythng.scala.json.EvtJsonProtocol._
import com.github.jeanadrien.evrythng.scala.json.{Property, Ref}
import com.typesafe.scalalogging.LazyLogging
import spray.json.JsValue

/**
  *
  */
class PropertyContext(private[rest] val contextRoot : String, propertyKey : String, val apiKey : String, projectScope : Option[Ref] = None) extends
    Environment with AuthorizedEnvironment with LazyLogging {

    def list = getPage[Property](s"${contextRoot}/properties/${propertyKey}")

    def create(value : List[Property]) = put[List[Property], List[Property]](s"${contextRoot}/properties/$propertyKey", value)

    def remove = delete(s"$contextRoot/properties/$propertyKey")

    def update(value : JsValue) = create(Property(value = value)::Nil)
}
