package com.github.jeanadrien.evrythng.scala.rest

import com.github.jeanadrien.evrythng.scala.json.{Product, Property}
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
class ProductPropertySpec(implicit val ee : ExecutionEnv) extends TestOperatorContext with BeforeAll {

    var containingProduct : Product = null

    override def beforeAll() : Unit = {
        super.beforeAll()

        Await.ready(
            operator.products.create(Product(name = Some("TestPropertyProduct"))).exec.map {
                containingProduct = _
            }, 5 seconds
        )
    }

    val propertyKey1 = Random.alphanumeric.take(8).mkString.toLowerCase
    val propertyKey2 = Random.alphanumeric.take(8).mkString.toLowerCase

    def randomProperty(key : String) = Property(key = Some(key), value = JsString(Random.nextString(10)))

    var theProperties : List[Property] = List()

    override def is : SpecStructure =
        sequential ^
            s2"""
            The Product Properties API is able
                        to add different properties to a Product              $addMutipleProperties
                        and see them                                          $loadProductProperties
                        to add a new value to a property to the Product       $updatePropertyKey2
                        and see it                                            $loadProperty2History
                        to delete properties having a given key               $deleteProperty2
                        and see the difference at the key level               $loadProperty2History
                        and at the root context level                         $loadProductProperties
                        to delete all properties                              $deleteAllProperties
                        and see it's empty                                    $loadProductProperties
          """

    def addProductProperties(props : List[Property]) =
        operator.product(containingProduct.id.get).properties.create(props).exec map { created =>
            theProperties = theProperties ::: created
            created.map(_.copy(timestamp = None))
        } must containTheSameElementsAs(props).await

    def loadProductProperties = operator.product(containingProduct.id.get).properties.list.exec map { page =>
        page.items
    } must containTheSameElementsAs(theProperties).await

    def updateProperty(key : String, value : String) =
        operator.product(containingProduct.id.get).properties(key).update(JsString(value)).exec map { updated : List[Property] =>
            theProperties = updated ::: theProperties
            updated.map(_.copy(timestamp = None))
        } must containTheSameElementsAs(Property(key = Some(key), value = JsString(value))::Nil).await

    def loadPropertyHistory(key : String) = operator.product(containingProduct.id.get).properties(key).list.exec map { page =>
        page.items
    } must containTheSameElementsAs(theProperties.filter(_.key == Some(key)).map(_.copy(key = None))).await

    def deletePropertiesOfKey(key : String) = operator.product(containingProduct.id.get).properties(key).remove.exec map { _ =>
        theProperties = theProperties.filterNot(_.key == Some(key))
        ()
    } must beEqualTo(()).await

    def deleteAllProperties = operator.product(containingProduct.id.get).properties.remove.exec map { _ =>
        theProperties = Nil
        ()
    } must beEqualTo(()).await

    def addMutipleProperties = addProductProperties(randomProperty(propertyKey1)::randomProperty(propertyKey2)::Nil)
    def updatePropertyKey2 = updateProperty(propertyKey2, Random.nextString(10))
    def loadProperty2History = loadPropertyHistory(propertyKey2)
    def deleteProperty2 = deletePropertiesOfKey(propertyKey2)
}
