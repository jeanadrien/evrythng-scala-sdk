package com.github.jeanadrien.evrythng.scala.json

import spray.json.JsValue

/**
  * Collection = {
  *"id": Ref,
  *"createdAt": Timestamp,
  *"updatedAt": Timestamp,
  *"name": String,
  *"description": String,
  *"collections": [ Ref, ... ],
  *"customFields": {
    *Key: Value,
    *...
  *},
  *"tags": [ String, ... ],
  *"identifiers": {
    *Key: Value, // both key and value should be Strings
    *...
  *}
*}
 */
case class Collection(
     name : Option[String] = None,
     description : Option[String] = None,
     id : Option[Ref]  = None,
	createdAt : Option[Long] = None,
	updatedAt : Option[Long] = None,
  collections : Option[List[Ref]] = None,
  customFields : Option[Map[String, JsValue]] = None,
  tags : Option[List[String]] = None,
  identifiers : Option[Map[String, String]] = None
)  {

}