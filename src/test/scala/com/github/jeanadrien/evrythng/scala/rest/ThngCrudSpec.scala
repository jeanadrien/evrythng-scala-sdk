package com.github.jeanadrien.evrythng.scala.rest

import com.github.jeanadrien.evrythng.scala.json.{Location, Thng}
import org.specs2.concurrent.ExecutionEnv
import spray.json._

import scala.concurrent.duration._
import scala.util.Random

/**
  *
  */
class ThngCrudSpec(implicit val ee : ExecutionEnv) extends TestOperatorContext {

    def is = sequential ^
        s2"""
        The Thng CRUD
            is creating a Thng          $createTheThng
            is reading the Thng         $readTheThng
            is updating the Thng        $updateTheThng
            see the updates             $readTheThng
            is deleting the Thng        $deleteTheThng
            don't see the Thng anymore  $missTheThng
    """

    var theThng : Thng = null
    val timeout = 2500 milliseconds

    private def normalizeComparison(t : Thng) = t.copy(properties = None, updatedAt = None)

    def createTheThng = {
        val randomName = Random.nextString(10)
        val randomDescription = Random.nextString(10)
        val randomLocation = Location.of(Random.nextDouble() * 50 + 10, Random.nextDouble() * 50 + 10)
        val randomCustomfieldKey1 = Random.nextString(10)
        val randomCustomfieldValue1 = JsString(Random.nextString(10))
        val randomCustomfieldKey2 = Random.nextString(10)
        val randomCustomfieldValue2 = JsNumber(Random.nextInt(5000))
        val randomCustomfieldKey3 = Random.nextString(10)
        val randomCustomfieldValue3 = JsBoolean(Random.nextBoolean())
        val randomCustomfieldKey4 = Random.nextString(10)
        val randomCustomfieldValue4 =
            """{
              | "aComplexValue" : ["a", "b", "c"]
              |}""".stripMargin.parseJson
        val randomIdentifierKey = Random.nextString(10)
        val randomIdentifierValue = Random.nextString(10)
        val randomTag1 = Random.nextString(4)
        val randomTag2 = Random.nextString(4)

        val newThng = Thng(
            name = Some(randomName),
            description = Some(randomDescription),
            location = Some(randomLocation),
            identifiers = Some(Map(randomIdentifierKey -> randomIdentifierValue)),
            tags = Some(randomTag1 :: randomTag2 :: Nil),
            customFields = Some(Map(
                randomCustomfieldKey1 -> randomCustomfieldValue1,
                randomCustomfieldKey2 -> randomCustomfieldValue2,
                randomCustomfieldKey3 -> randomCustomfieldValue3,
                randomCustomfieldKey4 -> randomCustomfieldValue4
            )
            )
        )

        operator.thngs.create(newThng).exec map { t =>
            theThng = t
            t.copy(id = None, createdAt = None, updatedAt = None)
        } must beEqualTo(newThng).awaitFor(timeout)
    }

    def readTheThng =
        operator.thngs.read(theThng.id.get).exec.map(normalizeComparison) must beEqualTo(normalizeComparison(theThng))
            .awaitFor(timeout)

    def updateTheThng = {
        val newRandomname = Random.nextString(10)

        operator.thngs.update(theThng.id.get, Thng(name = Some(newRandomname))).exec map { t =>
            theThng = t.copy(properties = None)
            t.name.get
        } must beEqualTo(newRandomname).awaitFor(timeout)
    }

    def deleteTheThng =
        operator.thngs.remove(theThng.id.get).exec must beEqualTo(()).awaitFor(timeout)

    def missTheThng =
        operator.thngs.read(theThng.id.get).exec must throwAn[EvtRequestException].like {
            case e : EvtRequestException => e.evtError.status must beEqualTo(404)
        }.awaitFor(timeout)

}
