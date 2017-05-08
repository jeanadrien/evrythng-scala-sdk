package com.github.jeanadrien.evrythng.scala.rest

import com.github.jeanadrien.evrythng.scala.config.TestSettings
import com.github.jeanadrien.evrythng.scala.json.Project
import com.typesafe.scalalogging.LazyLogging
import org.specs2.Specification
import org.specs2.concurrent.ExecutionEnv
import org.specs2.specification.BeforeAll

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.util.Random

/**
  *
  */
abstract class TestOperatorInScopeContext extends Specification with BeforeAll with TestSettings with LazyLogging {

    implicit val ee : ExecutionEnv

    val env = Environment

    var containingProject : Project = null

    val operatorKey = test.operator.apiKey

    var operator : OperatorContext = env.operatorApi(operatorKey)

    override def beforeAll() : Unit = Await.ready(for {
        project <- operator.projects.create(Project(
            name = Some(s"itproject-${Random.nextInt(200000)}")
        )).exec
    } yield {
        logger.info(s"New integration test project : ${project} with operator key ${operatorKey}")
        this.containingProject = project
        this.operator = operator.inProject(project.id.get)
        ()
    }, 10 seconds)
}
