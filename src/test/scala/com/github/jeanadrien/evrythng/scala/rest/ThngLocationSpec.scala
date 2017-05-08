package com.github.jeanadrien.evrythng.scala.rest

import com.github.jeanadrien.evrythng.scala.json.{Location, Thng}
import org.specs2.concurrent.ExecutionEnv
import org.specs2.specification.BeforeAll
import org.specs2.specification.core.SpecStructure

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.util.Random

/**
  *
  */
class ThngLocationSpec(implicit val ee : ExecutionEnv) extends TestOperatorContext with BeforeAll {

    var containingThng : Thng = null

    override def beforeAll() : Unit = {
        super.beforeAll()

        Await.ready(
            operator.thngs.create(Thng(name = Some("TestThng"))).exec.map {
                containingThng = _
            }, 5 seconds
        )
    }

    private var theLocations = List[Location]()

    val timeout = 2500 milliseconds

    val timestamp1 = 1486590570000L
    val timestamp2 = 1486590571000L
    val timestamp3 = 1486590572000L
    val timestamp4 = 1486590573000L
    val timestamp5 = 1486590574000L

    def randomLocation(ts : Long) = Location(
        latitude = Some(Random.nextDouble() * 160 - 80),
        longitude = Some(Random.nextDouble() * 340 - 170),
        timestamp = Some(ts)
    )

    override def is : SpecStructure =
        sequential ^
            s2"""
            The Thng Location  API is able
                        to add a Location to a Thng                 $addLocation1
                        and see it                                  $seeLocations
                        to add multiple Locations to the Thng       $addOtherLocations
                        and see them                                $seeLocations
                        to delete partially the Location            ${deleteLocations(Some(timestamp3))}
                        and see the difference                      $seeLocations
                        to delete all Location history              ${deleteLocations(None)}
                        and observe the now empty list              $seeLocations
          """

    def addLocations(locations : List[Location]) =
        operator.thng(containingThng.id.get).location.create(locations).exec.map { createdLocations =>
            theLocations = theLocations ++: createdLocations
            createdLocations
        } must containTheSameElementsAs(locations).awaitFor(timeout)

    def seeLocations =
        operator.thng(containingThng.id.get).location.list.exec.map { page =>
            page.items
        } must containTheSameElementsAs(theLocations).awaitFor(timeout)

    def deleteLocations(until : Option[Long]) =
        operator.thng(containingThng.id.get).location.remove(until).exec.map { _ =>
            theLocations = theLocations.filter(_.timestamp.get >= until.getOrElse(Long.MaxValue))
            ()
        } must beEqualTo(()).await

    def addLocation1 = addLocations(randomLocation(timestamp1) :: Nil)

    def addOtherLocations = addLocations(
        randomLocation(timestamp2) ::
            randomLocation(timestamp3) ::
            randomLocation(timestamp4) ::
            randomLocation(timestamp5) :: Nil
    )
}
