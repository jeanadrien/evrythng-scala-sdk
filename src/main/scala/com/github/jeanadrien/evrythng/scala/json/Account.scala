package com.github.jeanadrien.evrythng.scala.json

/**
  * {
  *   "id": Ref,
  *   "createdAt": Timestamp,
  *   "customFields": {
  *     Key: Value
  *   },
  *   "updatedAt": Timestamp,
  *   "name": String,
  *   "tfaRequired": Boolean
  * }
  */
case class Account(
    id        : Option[Ref],
    createdAt : Option[Long],
    updatedAt : Option[Long],
    name      : Option[String]
) {

}
