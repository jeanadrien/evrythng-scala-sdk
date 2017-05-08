package com.github.jeanadrien.evrythng.scala.json

/**
  *
  */
case class DeviceAuth(
    thngId : Option[Ref] = None,
    thngApiKey : Option[String] = None
)