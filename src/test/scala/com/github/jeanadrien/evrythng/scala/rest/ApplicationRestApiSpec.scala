package com.github.jeanadrien.evrythng.scala.rest

import com.github.jeanadrien.evrythng.scala.json.{Place, Product}
import org.specs2.concurrent.ExecutionEnv
import org.specs2.specification.BeforeAll

import scala.concurrent.Await
import scala.concurrent.duration._

/**
  *
  */
class ApplicationRestApiSpec(implicit val ee : ExecutionEnv) extends TestApplicationContext with BeforeAll {

    var testProductInScope : Product = null
    var testProductOutOfScope : Product = null
    var testPlaceInScope : Place = null;

    override def beforeAll() : Unit = {
        super.beforeAll()

        Await.ready(
            for {
                productOutOfScope <- operator.products.create(Product(name = Some("TestProductOutOfScope"))).exec
                productInScope <- operator.products.create(Product(name = Some("TestProductInScope")))
                    .project(containingProject).exec
                placeInScope <- operator.places.create(Place(name = Some("TestPlaceInScope")))
                    .project(containingProject).exec
            } yield {
                testProductOutOfScope = normProductComparison(productOutOfScope)
                testProductInScope = normProductComparison(productInScope)
                testPlaceInScope = placeInScope

                ()
            }
            , 5 seconds
        )
    }

    def is = sequential ^
        s2"""
        Using an application apiKey, it is possible to
            know who I am                          $knowWhoIAm
            see a Product in scope                 $seeProductInScope
            but not out of scope                   $missProductOutOfScope
            list the products in scope             $listProductsInScope
            read a place in scope                  $seePlaceInScope

    """

    def normProductComparison(p : Product) : Product = p.copy(properties = None)

    def knowWhoIAm =
        applicationApi.me.read.exec must beEqualTo(application).await

    def seeProductInScope =
        applicationApi.products.read(testProductInScope.id.get).exec.map(normProductComparison) must
            beEqualTo(testProductInScope).await

    def missProductOutOfScope =
        applicationApi.products.read(testProductOutOfScope.id.get).exec must throwAn[EvtRequestException].like {
            case e : EvtRequestException => e.evtError.status must beEqualTo(404)
        }.await

    def listProductsInScope =
        applicationApi.products.list.exec map (_.items.map(normProductComparison)) must
            containTheSameElementsAs(testProductInScope :: Nil).await

    def seePlaceInScope =
        applicationApi.places.read(testPlaceInScope.id.get).exec must beEqualTo(testPlaceInScope).await
}
