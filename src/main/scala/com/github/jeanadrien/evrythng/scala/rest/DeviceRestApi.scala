package com.github.jeanadrien.evrythng.scala.rest

import com.github.jeanadrien.evrythng.scala.json.EvtJsonProtocol._
import com.github.jeanadrien.evrythng.scala.json.{Ref, Thng}
import com.typesafe.scalalogging.LazyLogging

/**
  *
  */
class DeviceRestApi(override val apiKey : String, thngId : Ref) extends
    Environment with AuthorizedEnvironment with LazyLogging {

    parent =>

    val thng = new ThngContext(thngId, apiKey)

    val thngs = new {
        def read = get[Thng](s"/thngs/${thngId}")

        def update(diff : Thng) = put[Thng, Thng](s"/thngs/${thngId}", diff)
    }
}
