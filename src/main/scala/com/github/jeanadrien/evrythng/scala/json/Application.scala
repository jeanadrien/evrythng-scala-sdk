package com.github.jeanadrien.evrythng.scala.json

import spray.json.JsValue

/**
  * {
  *   "id": Ref,
  *   "createdAt": Timestamp,
  *   "updatedAt": Timestamp,
  *   "name":String,
  *   "description": String,
  *   "project": Ref,
  *   "defaultUrl": String,
  *   "socialNetworks": {
  *     "facebook": {
  *       "appId" : String,
  *       "appSecret" : String
  *     }
  *   },
  *   "tags": [ String, ... ],
  *   "customFields": {
  *     Key: Value,
  *     ...
  *   },
  *   "appApiKey" : String
  * }
  */
case class Application(
    id             : Option[Ref] = None,
    createdAt      : Option[Long] = None,
    updatedAt      : Option[Long] = None,
    name           : Option[String] = None,
    description    : Option[String] = None,
    project        : Option[Ref] = None,
    defaultUrl     : Option[String] = None,
    socialNetworks : Option[Map[String, JsValue]] = None,
    tags           : Option[List[String]] = None,
    customFields   : Option[Map[String, JsValue]] = None,
    appApiKey      : Option[String] = None
) {

}
