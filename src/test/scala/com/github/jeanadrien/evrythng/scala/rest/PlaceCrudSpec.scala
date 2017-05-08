package com.github.jeanadrien.evrythng.scala.rest

import com.github.jeanadrien.evrythng.scala.json.{Address, Place, Position}
import org.specs2.concurrent.ExecutionEnv
import spray.json.JsString

import scala.concurrent.duration._
import scala.util.Random

/**
  *
  */
class PlaceCrudSpec(implicit val ee: ExecutionEnv) extends TestOperatorContext {

    def is = sequential ^ s2"""
        The Place CRUD
            is creating a Place                  $createThePlace
            is reading the Place                 $readThePlace
            is geofiltering the Place            $geofilterThePlace
            is geofiltering missing the Place    $geofilterMissThePlace
            is updating the Place                $updateThePlace
            see the updates                      $readThePlace
            is deleting the Place                $deleteThePlace
            don't see the Place anymore          $missThePlace
    """

    var thePlace : Place = null
    val timeout = 2500 milliseconds

    val position = Position(10.4, 43.2)

    def createThePlace = {
        val randomName = Random.nextString(10)
        val randomDescription = Random.nextString(10)
        val randomAddress = Address(
            extension = Some(Random.nextString(10)),
            street = Some(Random.nextString(10)),
            postalCode = Some(Random.nextString(10)),
            city = Some(Random.nextString(10)),
            county = Some(Random.nextString(10)),
            state = Some(Random.nextString(10)),
            country = Some(Random.nextString(10)),
            countryCode = Some("CH"),
            district = Some(Random.nextString(10)),
            buildingName = Some(Random.nextString(10)),
            buildingFloor = Some(Random.nextString(10)),
            buildingRoom = Some(Random.nextString(10)),
            buildingZone = Some(Random.nextString(10)),
            crossing1 = Some(Random.nextString(10)),
            crossing2 = Some(Random.nextString(10))
        )
        val randomIcon = "http://myicon.nowehere.nw"
        val randomIdentifiers = Map(
            "rkey1" -> Random.nextString(5)
        )
        val randomCustomFields = Map(
            "cfkey1" -> JsString(Random.nextString(5))
        )
        val randomTags = Random.nextString(5)::Random.nextString(5)::Nil

        val newPlace = Place(
            name = Some(randomName),
            description = Some(randomDescription),
            address = Some(randomAddress),
            icon = Some(randomIcon),
            position = Some(position),
            tags = Some(randomTags),
            identifiers = Some(randomIdentifiers),
            customFields = Some(randomCustomFields)
        )

        operator.places.create(newPlace).exec map { p =>
            thePlace = p
            p.copy(createdAt = None, updatedAt = None, id = None)
        } must beEqualTo(newPlace).awaitFor(timeout)
    }

    def readThePlace =
        operator.places.read(thePlace.id.get).exec must beEqualTo(thePlace).awaitFor(timeout)

    def geofilterThePlace =
        operator.places.listAround(position.point, 0.5).exec.map(_.items) must beEqualTo(thePlace::Nil).await

    def geofilterMissThePlace =
        operator.places.listAround((-10.4, -43.2), 0.5).exec.map(_.items) must beEqualTo(Nil).await

    def updateThePlace = {
        val newRandomname = Random.nextString(10)

        operator.places.update(thePlace.id.get, Place(name = Some(newRandomname))).exec map { t =>
            thePlace = t
            t.name.get
        } must beEqualTo(newRandomname).awaitFor(timeout)
    }

    def deleteThePlace =
        operator.places.delete(thePlace.id.get).exec must beEqualTo(()).awaitFor(timeout)

    def missThePlace =
        operator.places.read(thePlace.id.get).exec must throwAn[EvtRequestException].like {
            case e : EvtRequestException => e.evtError.status must beEqualTo(404)
        }.awaitFor(timeout)

}

