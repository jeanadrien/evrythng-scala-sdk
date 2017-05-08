package com.github.jeanadrien.evrythng.scala.rest

import com.github.jeanadrien.evrythng.scala.json.EvtJsonProtocol._
import com.github.jeanadrien.evrythng.scala.json._
import com.typesafe.scalalogging.LazyLogging

/**
  *
  */
class UserRestApi(override val apiKey : String) extends
    Environment with AuthorizedEnvironment with LazyLogging {

    parent =>

    // thngs

    val thngs = new ResourceContext[Thng](this, "/thngs") with Cru[Thng]

    def thng(thngId : Ref) = new ThngContext(thngId, apiKey)

    // products

    val products = new ResourceContext[Product](this, "/products") with Cru[Product]

    def product(productId : Ref) = new ProductContext(productId, apiKey)

    // collections

    val collections = new ResourceContext[Collection](this, "/collections") with Cru[Collection]

    def collection(collectionId : Ref) = new CollectionContext(collectionId, apiKey)

    // actions

    val actions = new {

        def list = getPage[ActionType]("/actions")

        def apply(actionType : String) = new ResourceContext[Action](parent, s"/actions/${actionType}")
            with Read[Action] with Create[Action]

    }


    // auth

    val auth = new {
        val all = new {
            def logout = post[Unit, Unit]("/auth/all/logout", ())
        }

        // /evrythng/thngs

        val evrythng = new {
            val thngs = new {
                def create(thngId : Ref) = post[DeviceAuth, DeviceAuth]("/auth/evrythng/thngs", DeviceAuth(
                    thngId = Some(thngId)
                ))

                def read(thngId : Ref) = get[DeviceAuth](s"/auth/evrythng/thngs/${thngId}")

                def remove(thngId : Ref) = delete(s"/auth/evrythng/thngs/${thngId}")
            }
        }
    }

    // / places

    val places = new ResourceContext[Place](this, "/places") with Read[Place] {
        def listAround(position : (Double, Double), maxDist : Double) = list().queryParameter(
            "lat" -> position._2.toString,
            "lon" -> position._1.toString,
            "maxDist" -> maxDist.toString
        )
    }


}
