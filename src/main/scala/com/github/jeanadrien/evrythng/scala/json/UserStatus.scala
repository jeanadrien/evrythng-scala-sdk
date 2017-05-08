package com.github.jeanadrien.evrythng.scala.json

/**
  * {
  *   "evrythngUser": Ref,
  *   "activationCode": String,
  *   "status": String,
  *   "email": String
  * }
  */
case class UserStatus(
    evrythngUser   : Option[Ref] = None,
    evrythngApiKey : Option[String] = None,
    activationCode : Option[String] = None,
    status         : Option[String] = None,
    email          : Option[String] = None,
    activated      : Option[Boolean] = None
)
