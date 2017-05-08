package com.github.jeanadrien.evrythng.scala.json

import spray.json.JsValue

/**
  * {
  *   "id": Ref,
  *   "user": Ref,
  *   "createdAt": Ref,
  *   "createdByProject": Ref,
  *   "createdByApp": Ref,
  *   "type": String,
  *   "thng": Ref,
  *   "product": Ref,
  *   "collection": Ref,
  *   "timestamp": Timestamp,
  *   "identifiers": {
  *     Key: Value, // both key and value should be Strings
  *     ...
  *   },
  *   "location": {
  *     "position": {
  *       "type": "Point",
  *       "coordinates": [Number, Number]
  *     }
  *   },
  *   "locationSource": String,
  *   "context": {
  *     Key: Value,
  *    ...
  *   },
  *   "customFields":{
  *     Key: Value,
  *     ...
  *   },
  *   "tags": [String, ...]
  * }
  */
case class Action(
    id : Option[Ref] = None,
    user : Option[Ref] = None,
    createdAt : Option[Long] = None,
    createdByApp : Option[Ref] = None,
    createdByProject : Option[Ref] = None,
    `type` : Option[String] = None,
    thng  : Option[Ref] = None,
    product  : Option[Ref] = None,
    collection : Option[Ref] = None,
    timestamp : Option[Long] = None,
    identifiers : Option[Map[String, String]] = None,
    location : Option[Location] = None,
    locationSource : Option[String] = None,
    context : Option[Map[String, JsValue]] = None,
    customFields : Option[Map[String, JsValue]] = None,
    tags : Option[List[String]] = None
) {

}
