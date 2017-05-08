package com.github.jeanadrien.evrythng.scala.json

/**
  * {
    "id": "548583c4452cb8a6f27fd608",
    "account": "UeTbAWEC8BpwKF9aCrA5cc7s",
    "operator": "UeTbAWEC8BpwKF9aCrA5cc7s",
    "apiKey": "E7iHw7xrvgOEiDVBXGVgy2VMKvLWpWOtxE2v82DCJOGQpuv24MqFMTg23RWlxhtab1khZRqoGv1pgdOZ",
    "role": "admin"
  }
  */
case class AccountAccess(
    id : Option[Ref] = None,
    account : Option[Ref] = None,
    operator : Option[Ref] = None,
    apiKey : Option[String] = None,
    role : Option[String] = None
) {

}
