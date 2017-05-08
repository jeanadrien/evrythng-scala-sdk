package com.github.jeanadrien.evrythng.scala.rest

import com.github.jeanadrien.evrythng.scala.json.EvtJsonProtocol._
import com.github.jeanadrien.evrythng.scala.json.{Ref, SecretKey}
import com.typesafe.scalalogging.LazyLogging

/**
  *
  */
class ApplicationContext(projectId : Ref, applicationId : Ref, val apiKey : String) extends
    Environment with AuthorizedEnvironment with LazyLogging {

    val secretKey = new {
        def read = get[SecretKey](s"/projects/${projectId}/applications/${applicationId}/secretKey")
    }

}
