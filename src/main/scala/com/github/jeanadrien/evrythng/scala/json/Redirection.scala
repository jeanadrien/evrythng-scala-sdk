package com.github.jeanadrien.evrythng.scala.json

/**
  * {
  *   "createdAt": Timestamp,
  *   "updatedAt": Timestamp,
  *   "shortDomain": String,
  *   "shortId": String,
  *   "defaultRedirectUrl": String,
  *   "redirectUrl": String,
  *   "evrythngId": Ref,
  *   "type": String,
  *   "evrythngUrl": String,
  *   "hits": Integer
  * }
  */
case class Redirection(
    createdAt : Option[Long] = None,
    updatedAt : Option[Long] = None,
    shortDomain : Option[String] = None,
    shortId : Option[String] = None,
    defaultRedirectUrl : Option[String] = None,
    evrythngId : Option[Ref] = None,
    `type` : Option[String] = None,
    evrythngUrl : Option[String] = None,
    hits : Option[Int] = None
)
