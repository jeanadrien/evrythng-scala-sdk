package com.github.jeanadrien.evrythng.scala.rest

import com.github.jeanadrien.evrythng.scala.json.Project
import org.specs2.concurrent.ExecutionEnv
import org.specs2.specification.core.SpecStructure
import spray.json.JsString

import scala.concurrent.duration._
import scala.util.Random

/**
  *
  */
class ProjectCrudSpec(implicit val ee : ExecutionEnv) extends TestOperatorContext {

    private var theProject : Project = null

    val timeout = 2500 milliseconds

    private def normalizeComparison(p : Project) = p.copy(updatedAt = None)

    override def is : SpecStructure =
        sequential ^
            s2"""
            The Project CRUD API is able
                        to create a Project                  $createTheProject
                        to read a Project                    $readTheProject
                        to update a Project                  $updateTheProject
                        and see the updates                  $readTheProject
                        to delete the Project                $deleteTheProject
                        and don't see the Project anymore    $missTheProject
          """

    def createTheProject = {
        val randomName = Random.nextString(10)
        val randomDescription = Random.nextString(10)

        val newProject = Project(
            name = Some(randomName),
            description = Some(randomDescription),
            imageUrl = Some("http://image.url"),
            tags = Some("t1" :: "t3" :: Nil),
            identifiers = Some(Map("id1" -> "val1", "id2" -> "val2")),
            customFields = Some(Map("cf1" -> JsString("cf1val"), "cf2" -> JsString("cf2val")))
        )

        logger.debug(s"Create new project: ${newProject}")

        operator.projects.create(newProject).exec map { t =>
            theProject = t
            t.copy(id = None, createdAt = None, updatedAt = None)
        } must beEqualTo(newProject).awaitFor(timeout)
    }

    def readTheProject =
        operator.projects.read(theProject.id.get).exec
            .map(normalizeComparison) must beEqualTo(normalizeComparison(theProject)).awaitFor(timeout)

    def updateTheProject = {
        val newRandomname = Random.nextString(10)

        operator.projects.update(theProject.id.get, Project(name = Some(newRandomname))).exec map { t =>
            theProject = t
            t.name.get
        } must beEqualTo(newRandomname).awaitFor(timeout)
    }

    def deleteTheProject =
        operator.projects.remove(theProject.id.get).exec must beEqualTo(()).awaitFor(timeout)

    def missTheProject =
        operator.projects.read(theProject.id.get).exec must throwAn[EvtRequestException].like {
            case e : EvtRequestException => e.evtError.status must beEqualTo(404)
        }.awaitFor(timeout)


}
