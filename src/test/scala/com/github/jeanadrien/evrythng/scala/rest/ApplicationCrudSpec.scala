package com.github.jeanadrien.evrythng.scala.rest

import com.github.jeanadrien.evrythng.scala.json.{Application, Project}
import org.specs2.concurrent.ExecutionEnv
import org.specs2.specification.BeforeAll
import org.specs2.specification.core.SpecStructure
import spray.json.JsString

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.util.Random

/**
  *
  */
class ApplicationCrudSpec(implicit val ee : ExecutionEnv) extends TestOperatorContext with BeforeAll {

    var containingProject : Project = null

    override def beforeAll() : Unit = {
        super.beforeAll()

        Await.ready(
            operator.projects.create(Project(name = Some("TestProject"))).exec.map {
                containingProject = _
            }, 5 seconds
        )
    }

    private var theApplication : Application = null

    val timeout = 2500 milliseconds

    private def normalizeComparison(p : Application) = p.copy(updatedAt = None, project = None)

    override def is : SpecStructure =
        sequential ^
            s2"""
            The Application CRUD API is able
                        to create a Application                  $createTheApplication
                        to read a Application                    $readTheApplication
                        to update a Application                  $updateTheApplication
                        and see the updates                      $readTheApplication
                        to delete the Application                $deleteTheApplication
                        and don't see the Application anymore    $missTheApplication
          """

    def createTheApplication = {
        val randomName = Random.nextString(10)
        val randomDescription = Random.nextString(10)

        val newApplication = Application(
            name = Some(randomName),
            description = Some(randomDescription),
            defaultUrl = Some("http://default.url"),
            socialNetworks = Some(Map()),
            tags = Some("t1"::"t3"::Nil),
            customFields = Some(Map("cf1" -> JsString("cf1val"), "cf2" -> JsString("cf2val")))
        )

        logger.debug(s"Create new application: ${newApplication}")

        operator.project(containingProject.id.get).applications.create(newApplication).exec map { t =>
            theApplication = t
            t.copy(id = None, createdAt = None, updatedAt = None, project = None, appApiKey = None)
        } must beEqualTo(newApplication).awaitFor(timeout)
    }

    def readTheApplication =
        operator.project(containingProject.id.get).applications.read(theApplication.id.get).exec
            .map(normalizeComparison) must beEqualTo(normalizeComparison(theApplication)).awaitFor(timeout)

    def updateTheApplication = {
        val newRandomname = Random.nextString(10)

        operator.project(containingProject.id.get).applications.update(theApplication.id.get, Application(name = Some(newRandomname))).exec map { t =>
            theApplication = t
            t.name.get
        } must beEqualTo(newRandomname).awaitFor(timeout)
    }

    def deleteTheApplication =
        operator.project(containingProject.id.get).applications.remove(theApplication.id.get).exec must beEqualTo(()).awaitFor(timeout)

    def missTheApplication =
        operator.project(containingProject.id.get).applications.read(theApplication.id.get).exec must throwAn[EvtRequestException].like {
            case e : EvtRequestException => e.evtError.status must beEqualTo(404)
        }.awaitFor(timeout)


}
