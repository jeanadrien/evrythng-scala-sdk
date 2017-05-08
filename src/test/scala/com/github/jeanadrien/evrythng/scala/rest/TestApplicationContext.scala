package com.github.jeanadrien.evrythng.scala.rest

import com.github.jeanadrien.evrythng.scala.json.{Application, Project}
import org.specs2.concurrent.ExecutionEnv
import org.specs2.specification.BeforeAll

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.util.Random

/**
  *
  */
abstract class TestApplicationContext extends TestOperatorContext with BeforeAll {

    implicit val ee : ExecutionEnv

    var applicationApi : ApplicationRestApi = null
    var containingProject : Project = null
    var application : Application = null

    override def beforeAll() : Unit = Await.ready( for {
        project <- operator.projects.create(Project(
            name = Some(s"itproject-${Random.nextInt(200000)}")
        )).exec
        application <- operator.project(project.id.get).applications.create(Application(
            name = Some(s"itapp-${Random.nextInt(200000)}"),
            socialNetworks = Some(Map())
        )).exec
    } yield {
        logger.info(s"New integration test application : ${application} with operator key ${operatorKey}")
        this.application = application
        this.applicationApi = env.applicationApi(application)
        this.containingProject = project
        ()
    } , 10 seconds)

}
