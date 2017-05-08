package com.github.jeanadrien.evrythng.scala.json

import spray.json.JsValue

/**
  * ProductDocument = {
  *"id": Ref,
  *"createdAt": Timestamp,
  *"updatedAt": Timestamp,
 	*"name": String,
  *"description": String,
  *"brand": String,
  *"categories": [ String, ...],
  *"photos": [ String, ... ],    // list of URLs
  *"url": String,
  *"identifiers": {
    *Key: Value,                 // both key and value should be Strings
    *...
  *},
  *"properties": {
    *Key: Value,
    *...
  *},
  *"tags": [ String, ... ],
  *"customFields": {
    *Key: Value,
    *...
  *}
*}
  */
case class Product(
    id : Option[Ref] = None,
    createdAt : Option[Long] = None,
    updatedAt : Option[Long] = None,
    name : Option[String],
    description : Option[String] = None,
    brand : Option[String] = None,
    categories : Option[List[String]] = None,
    url : Option[String] = None,
    photos : Option[List[String]] = None,
    identifiers : Option[Map[String, String]] = None,
    properties : Option[Map[String, JsValue]] = None,
    tags : Option[List[String]] = None,
    customFields : Option[Map[String, JsValue]] = None
) {

}
