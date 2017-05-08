package com.github.jeanadrien.evrythng.scala.json

/**
  * {
  *    "extension": String,
  *    "street": String,
  *    "postalCode": String,
  *    "city": String,
  *    "county": String,
  *    "state": String,
  *    "country": String,
  *    "countryCode": String,
  *    "district": String,
  *    "buildingName": String,
  *    "buildingFloor": String,
  *    "buildingRoom": String,
  *    "buildingZone": String,
  *    "crossing1": String,
  *    "crossing2": String
  * }
  */
case class Address(
    extension     : Option[String] = None,
    street        : Option[String] = None,
    postalCode    : Option[String] = None,
    city          : Option[String] = None,
    county        : Option[String] = None,
    state         : Option[String] = None,
    country       : Option[String] = None,
    countryCode   : Option[String] = None,
    district      : Option[String] = None,
    buildingName  : Option[String] = None,
    buildingFloor : Option[String] = None,
    buildingRoom  : Option[String] = None,
    buildingZone  : Option[String] = None,
    crossing1     : Option[String] = None,
    crossing2     : Option[String] = None
) {

}
