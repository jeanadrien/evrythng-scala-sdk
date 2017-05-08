package com.github.jeanadrien.evrythng.scala.rest

import com.github.jeanadrien.evrythng.scala.json.Collection
import org.specs2.concurrent.ExecutionEnv
import org.specs2.specification.core.SpecStructure

import scala.concurrent.duration._
import scala.util.Random

/**
  *
  */
class CollectionCrudSpec(implicit val ee : ExecutionEnv) extends TestOperatorContext {

    private var theCollection : Collection = null

    val timeout = 2500 milliseconds

    private def normalizeComparison(t : Collection) = t.copy(updatedAt = None)

    override def is : SpecStructure =
        sequential ^
            s2"""
            The Collection CRUD API is able
                        to create a Collection                  $createTheCollection
                        to read a Collection                    $readTheCollection
                        to update a Collection                  $updateTheCollection
                        and see the updates                     $readTheCollection
                        to delete the Collection                $deleteTheCollection
                        and don't see the Collection anymore    $missTheCollection
          """

    def createTheCollection = {
        val randomName = Random.nextString(10)
        val randomDescription = Random.nextString(10)

        val newCollection = Collection(
            name = Some(randomName),
            description = Some(randomDescription)
        )

        logger.debug(s"Create new collection: ${newCollection}")

        operator.collections.create(newCollection).exec map { t =>
            theCollection = t
            t.copy(id = None, createdAt = None, updatedAt = None)
        } must beEqualTo(newCollection).awaitFor(timeout)
    }

    def readTheCollection =
        operator.collections.read(theCollection.id.get).exec
            .map(normalizeComparison) must beEqualTo(normalizeComparison(theCollection)).awaitFor(timeout)

    def updateTheCollection = {
        val newRandomname = Random.nextString(10)

        operator.collections.update(theCollection.id.get, Collection(name = Some(newRandomname))).exec map { t =>
            theCollection = t
            t.name.get
        } must beEqualTo(newRandomname).awaitFor(timeout)
    }

    def deleteTheCollection =
        operator.collections.delete(theCollection.id.get).exec must beEqualTo(()).awaitFor(timeout)

    def missTheCollection =
        operator.collections.read(theCollection.id.get).exec must throwAn[EvtRequestException].like {
            case e : EvtRequestException => e.evtError.status must beEqualTo(404)
        }.awaitFor(timeout)
}
