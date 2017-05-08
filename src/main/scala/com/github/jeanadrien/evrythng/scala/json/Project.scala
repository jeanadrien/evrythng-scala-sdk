package com.github.jeanadrien.evrythng.scala.json

import spray.json.JsValue

/**
  * {
  * "id": Ref,
  *"createdAt": Timestamp,
  *"updatedAt": Timestamp,
  *"name": String,
  *"description": String,
  *"imageUrl": String,
  *"tags": [ String, ... ],
  *"identifiers": {
    *Key: Value,
    *...
  *},
  *"customFields": {
    *Key: Value,
    *...
  *}
*}
  */
case class Project(
    id : Option[Ref] = None,
    createdAt : Option[Long] = None,
    updatedAt : Option[Long] = None,
    name : Option[String] = None,
    description : Option[String] = None,
    imageUrl : Option[String] = None,
    tags : Option[List[String]] = None,
    identifiers : Option[Map[String, String]] = None,
    customFields : Option[Map[String, JsValue]] = None
) {

}
