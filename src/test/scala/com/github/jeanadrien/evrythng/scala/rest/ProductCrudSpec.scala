package com.github.jeanadrien.evrythng.scala.rest

import com.github.jeanadrien.evrythng.scala.json.Product
import org.specs2.concurrent.ExecutionEnv

import scala.concurrent.duration._
import scala.util.Random

/**
  *
  */
class ProductCrudSpec(implicit val ee : ExecutionEnv) extends TestOperatorContext {

    def is = sequential ^
        s2"""
        The Product CRUD
            is creating a Product          $createTheProduct
            is reading the Product         $readTheProduct
            is updating the Product        $updateTheProduct
            see the updates                $readTheProduct
            is deleting the Product        $deleteTheProduct
            don't see the Product anymore  $missTheProduct
    """

    var theProduct : Product = null
    val timeout = 2500 milliseconds

    private def normalizeComparison(t : Product) = t.copy(properties = None, updatedAt = None)

    def createTheProduct = {
        val randomName = Random.nextString(10)
        val randomDescription = Random.nextString(10)
        val randomBrand = Random.nextString(10)
        val randomCategorie1 = Random.nextString(5)
        val randomCategorie2 = Random.nextString(5)
        val randomUrl = s"http://nowhere.nw/${Random.nextInt(500)}.jpg"
        val randomPhotoUrl = s"http://nowhere.nw/${Random.nextInt(500)}.jpg"

        val newProduct = Product(
            name = Some(randomName),
            description = Some(randomDescription),
            brand = Some(randomBrand),
            categories = Some(randomCategorie1 :: randomCategorie2 :: Nil),
            url = Some(randomUrl),
            photos = Some(randomPhotoUrl :: Nil)
        )

        operator.products.create(newProduct).exec map { p =>
            theProduct = p
            p.copy(createdAt = None, updatedAt = None, id = None)
        } must beEqualTo(newProduct).awaitFor(timeout)
    }

    def readTheProduct =
        operator.products.read(theProduct.id.get).exec
            .map(normalizeComparison) must beEqualTo(normalizeComparison(theProduct)).awaitFor(timeout)

    def updateTheProduct = {
        val newRandomname = Random.nextString(10)

        operator.products.update(theProduct.id.get, Product(name = Some(newRandomname))).exec map { t =>
            theProduct = t
            t.name.get
        } must beEqualTo(newRandomname).awaitFor(timeout)
    }

    def deleteTheProduct =
        operator.products.remove(theProduct.id.get).exec must beEqualTo(()).awaitFor(timeout)

    def missTheProduct =
        operator.products.read(theProduct.id.get).exec must throwAn[EvtRequestException].like {
            case e : EvtRequestException => e.evtError.status must beEqualTo(404)
        }.awaitFor(timeout)
}
