package com.github.jeanadrien.evrythng.scala.rest

import com.github.jeanadrien.evrythng.scala.json.EvtJsonProtocol._
import com.github.jeanadrien.evrythng.scala.json.{Application, Ref}
import com.typesafe.scalalogging.LazyLogging

/**
  *
  */
class ProjectContext(val apiKey : String, projectId : Ref) extends
    Environment with AuthorizedEnvironment with LazyLogging {

    self =>

    val applications = new ResourceContext[Application](this, s"/projects/${projectId}/applications") with Crud[Application]

    def application(id : Ref) = new ApplicationContext(projectId, id, apiKey)

}
