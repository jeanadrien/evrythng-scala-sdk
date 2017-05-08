package com.github.jeanadrien.evrythng.scala.json

import spray.json.JsValue

/**
  * {
  *   "id": Ref,
  *   "name": String,
  *   "createdAt": Timestamp,
  *   "updatedAt": Timestamp,
  *   "customFields": {
  *     Key: Value,
  *     ...
  *   },
  *   "tags": [ String, ... ]
*}
  */
case class ActionType(
    id : Option[String] = None, // TODO try with Ref
    name : Option[String] = None,
    createdAt : Option[Long] = None,
    updatedAt : Option[Long] = None,
    customFields : Option[Map[String, JsValue]] = None,
    tags : Option[List[String]] = None
) {

}
