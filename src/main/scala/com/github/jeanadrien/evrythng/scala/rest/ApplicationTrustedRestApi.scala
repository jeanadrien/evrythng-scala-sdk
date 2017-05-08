package com.github.jeanadrien.evrythng.scala.rest

import com.github.jeanadrien.evrythng.scala.json.EvtJsonProtocol._
import com.github.jeanadrien.evrythng.scala.json._
import com.typesafe.scalalogging.LazyLogging

/**
  *
  */
class ApplicationTrustedRestApi(override val apiKey : String) extends
    Environment with AuthorizedEnvironment with LazyLogging {

    parent =>

    val me = new {
        def read = get[Application]("/applications/me")
    }

    // auth
    object auth {
        object evrythng {
            object users {
                def create(user : User) = post[User, UserStatus]("/auth/evrythng/users", user)

                def createAnonymous() =
                    post[User, UserStatus]("/auth/evrythng/users", User()).queryParameter("anonymous" -> "true")

                def apply(userId : Ref) = new {
                    def validate(activationCode : String) =
                        post[UserStatus, UserStatus](s"/auth/evrythng/users/${userId}/validate",
                            UserStatus(activationCode = Some(activationCode)))
                }
            }

            def login(email : String, password : String) =
                post[User, UserStatus]("/auth/evrythng", User(email = Some(email), password = Some(password)))
        }
    }

    // products
    val products = new ResourceContext[Product](this, "/products") with Crud[Product]

    def product(productId : Ref) = new ProductContext(productId, apiKey)

    // actions

    def action(actionType : String) = new ActionContext("", actionType, apiKey)

    // TODO /scan

    // places

    val places = new ResourceContext[Place](this, "/places") with Crud[Place] {
        def listAround(position : (Double, Double), maxDist : Double) = list().queryParameter(
            "lat" -> position._2.toString,
            "lon" -> position._1.toString,
            "maxDist" -> maxDist.toString
        )
    }

    // thngs

    val thngs = new ResourceContext[Thng](this, "/thngs") with Crud[Thng]

    def thng(thngId : Ref) = new ThngContext(thngId, apiKey)

    // collections

    val collections = new ResourceContext[Collection](this, "/collections") with Crud[Collection]

    def collection(collectionId : Ref) = new CollectionContext(collectionId, apiKey)

    // users

    def users = new ResourceContext[User](this, "/users") with Read[User]
    // actions

    // TODO remove this class, and replace ActionContext with ResourceContext
    val actions = new ActionTypesContext(apiKey)

    // TODO reactor


}
