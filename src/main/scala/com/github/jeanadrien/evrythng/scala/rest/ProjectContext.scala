package com.github.jeanadrien.evrythng.scala.rest

import com.github.jeanadrien.evrythng.scala.json.EvtJsonProtocol._
import com.github.jeanadrien.evrythng.scala.json.{Application, Ref}
import com.typesafe.scalalogging.LazyLogging
import spray.json.JsonFormat

/**
  *
  */
class ProjectContext(val apiKey : String, projectId: Ref) extends
    Environment with AuthorizedEnvironment with LazyLogging {

    self =>

    val applications = new CrudResource[Application] {
        val format = implicitly[JsonFormat[Application]]

        val env = self
        val urlPath = s"/projects/${projectId}/applications"
    }

    def application(id : Ref) = new ApplicationContext(projectId, id, apiKey)

}
