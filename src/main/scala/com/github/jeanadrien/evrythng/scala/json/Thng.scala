package com.github.jeanadrien.evrythng.scala.json

import spray.json._

/**
  * ThngDocument = {
  * "id": Ref,
  * "createdAt": Timestamp,
  * "updatedAt": Timestamp,
  * "name": String,
  * "description": String,
  * "product": Ref,
  * "location": Location,
  * "identifiers": {
  * Key: Value,              // both key and value should be Strings
  * ...
  * },
  * "properties": {
  * Key: Value,
  * ...
  * },
  * "tags": [ String, ... ],
  * "collections": [ Ref, ... ],
  * "customFields": {
  * Key: Value,
  * ...
  * }
  * }
  */
case class Thng(
    name         : Option[String] = None,
    description  : Option[String] = None,
    product      : Option[Ref] = None,
    location     : Option[Location] = None,
    identifiers  : Option[Map[String, String]] = None,
    properties   : Option[Map[String, JsValue]] = None,
    customFields : Option[Map[String, JsValue]] = None,
    tags         : Option[List[String]] = None,
    collections  : Option[List[Ref]] = None,
    id           : Option[Ref] = None,
    updatedAt    : Option[Long] = None,
    createdAt    : Option[Long] = None
) {

}
