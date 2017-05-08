package com.github.jeanadrien.evrythng.scala.json

import spray.json.JsValue

/**
  * {
  *   "email": String,
  *   "firstName": String,
  *   "lastName": String,
  *   "password": String,
  *   "birthday": {
  *     "day": Integer,
  *     "month": Integer,
  *     "year": Integer
  *   },
  *   "gender": "male"|"female",
  *   "timezone": String,
  *   "locale": String,
  *   "photo": String, // base64 encoded picture
  *   "customFields": {
  *     Key: Value, // where key and value are Strings
  *     ...
  *   },
  *   "tags": [ String, ... ]
  * }
  */
case class User(
    email        : Option[String] = None,
    firstName    : Option[String] = None,
    lastName     : Option[String] = None,
    password     : Option[String] = None,
    birthday     : Option[Birthday] = None,
    gender       : Option[String] = None,
    timezone     : Option[String] = None,
    locale       : Option[String] = None,
    photo        : Option[String] = None,
    customFields : Option[Map[String, JsValue]] = None,
    tags         : Option[List[String]] = None
)
