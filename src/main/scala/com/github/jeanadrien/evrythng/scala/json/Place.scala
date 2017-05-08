package com.github.jeanadrien.evrythng.scala.json

import spray.json.JsValue

/**
  * {
  *   "id": Ref,
  *   "createdAt": Timestamp,
  *   "updatedAt": Timestamp,
  *   "name": String,
  *   "position": GeoJSON {
  *     "type": "Point",
  *     "coordinates": [Double, Double]
  *   },
  *   "address": {
  *     "extension": String,
  *     "street": String,
  *     "postalCode": String,
  *     "city": String,
  *     "county": String,
  *     "state": String,
  *     "country": String,
  *     "countryCode": String,
  *     "district": String,
  *     "buildingName": String,
  *     "buildingFloor": String,
  *     "buildingRoom": String,
  *     "buildingZone": String,
  *     "crossing1": String,
  *     "crossing2": String
  *   }
  *   "description": String,
  *   "icon": String,
  *   "tags": [String, ...],
  *   "identifiers": {
  *     Key: Value, // both key and value should be Strings
  *     ...
  *   },
  *   "customFields": {
  *     Key: Value, // Value can by any json
  *     ...
  *   }
  * }
  */
case class Place(
    id           : Option[Ref] = None,
    createdAt    : Option[Long] = None,
    updatedAt    : Option[Long] = None,
    name         : Option[String] = None,
    position     : Option[Position] = None,
    address      : Option[Address] = None,
    description  : Option[String] = None,
    icon         : Option[String] = None,
    tags         : Option[List[String]] = None,
    identifiers  : Option[Map[String, String]] = None,
    customFields : Option[Map[String, JsValue]] = None
) {

}
