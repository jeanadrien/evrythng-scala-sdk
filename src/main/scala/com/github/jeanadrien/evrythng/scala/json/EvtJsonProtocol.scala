package com.github.jeanadrien.evrythng.scala.json

import spray.json._

/**
  *
  */
object EvtJsonProtocol extends DefaultJsonProtocol {

    implicit object RefJsonFormat extends JsonFormat[Ref] {
        def write(ref: Ref) = JsString(ref.id)
        def read(value : JsValue) = value match {
            case JsString(str) => Ref(str)
            case _ => deserializationError("Ref must be a String")
        }
    }

    implicit object PositionFormat extends JsonFormat[Position] {
        def write(ref: Position) = JsObject(
            "coordinates" -> JsArray(JsNumber(ref.x), JsNumber(ref.y))
        )
        def read(value : JsValue) = value match {
            case JsObject(content) if content.contains("coordinates") =>
                content.apply("coordinates") match {
                    case JsArray(Vector(JsNumber(x), JsNumber(y))) =>
                        Position(x.toDouble, y.toDouble)
                    case _ =>
                        deserializationError("position.coordinates must be an array with two numbers")
                }
            case _ =>
                deserializationError("position must be an object with some coordinates")
        }
    }

    implicit val locationFormat = jsonFormat3(Location.apply)
    implicit val thngFormat = jsonFormat12(Thng)
    implicit val collectionFormat = jsonFormat9(Collection)
    implicit val propertyFormat = jsonFormat3(Property)
    implicit val productFormat = jsonFormat13(Product)
    implicit val projectFormat = jsonFormat9(Project)
    implicit val accountAccessFormat = jsonFormat5(AccountAccess)
    implicit val evtErrorFormat = jsonFormat4(EvtError)
    implicit val actionTypeFormat = jsonFormat6(ActionType)
    implicit val actionFormat = jsonFormat16(Action)
    implicit val applicationFormat = jsonFormat11(Application)
    implicit val addressFormat = jsonFormat15(Address)
    implicit val placeFormat = jsonFormat11(Place)
    implicit val birthdayFormat = jsonFormat3(Birthday)
    implicit val userFormat = jsonFormat11(User)
    implicit val userstatusFormat = jsonFormat6(UserStatus)
    implicit val deviceAuthFormat = jsonFormat2(DeviceAuth)
    implicit val secretKeyFormat = jsonFormat1(SecretKey)
    implicit val timeFormat = jsonFormat4(Time)
    implicit val redirectionFormat = jsonFormat9(Redirection)

}
