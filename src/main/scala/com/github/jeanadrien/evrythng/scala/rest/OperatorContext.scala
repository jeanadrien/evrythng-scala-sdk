package com.github.jeanadrien.evrythng.scala.rest

import java.io.ByteArrayInputStream
import javax.imageio.ImageIO

import com.github.jeanadrien.evrythng.scala.json.EvtJsonProtocol._
import com.github.jeanadrien.evrythng.scala.json._
import com.typesafe.scalalogging.LazyLogging
import spray.json.JsonFormat

/**
  *
  */
class OperatorContext(val apiKey : String, projectScope : Option[Ref] = None) extends
Environment with AuthorizedEnvironment with LazyLogging {

    // TODO rename
    // TODO should be Operator root context (or root context)
    // TODO : This contains two things. The Root context with the Operator
    self =>

    override def defaultQueryParams : Seq[(String, String)] = super
        .defaultQueryParams ++ projectScope.map("project" -> _.toString)

    // thngs

    val thngs = new CrudResource[Thng] {
        val format = implicitly[JsonFormat[Thng]]

        val env = self
        val urlPath = "/thngs"

    }

    def thng(thngId : Ref) = new ThngContext(thngId, apiKey, projectScope)


    // products

    val products = new ResourceContext[Product](this, "/products") with Crud[Product]

    def product(productId : Ref) = new ProductContext(productId, apiKey, projectScope)

    // collections

    val collections = new CrudResource[Collection] {
        val format = implicitly[JsonFormat[Collection]]

        val env = self
        val urlPath = "/collections"
    }

    def collection(collectionId : Ref) = new CollectionContext(collectionId, apiKey, projectScope)

    // projects

    val projects = new CrudResource[Project] {
        val format = implicitly[JsonFormat[Project]]

        val env = self
        val urlPath = "/projects"
    }

    def project(projectId : Ref) = new ProjectContext(apiKey, projectId)

    // actions

    // TODO remove this class, and replace ActionContext with ResourceContext
    val actions = new ActionTypesContext(apiKey, projectScope)

    // places

    object places extends CrudResource[Place] {
        val format = implicitly[JsonFormat[Place]]

        val env = self
        val urlPath = "/places"

        def listAround(position : (Double, Double), maxDist : Double) = list().queryParameter(
            "lat" -> position._2.toString,
            "lon" -> position._1.toString,
            "maxDist" -> maxDist.toString
        )
    }

    // users

    val users = new ResourceContext[User](this, "/users") with Read[User] with Update[User] with Delete[User]

    // auth/evrythng/thng

    val auth = new {
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

    // redirections

    val redirections = new {
        def create(redirection : Redirection) = shortDomain.post[Redirection, Redirection]("/redirections", redirection)

        def read(shortId : String) = shortDomain.get[Redirection](s"/redirections/${shortId}")

        def qr(shortId : String) = shortDomain
            .getData(s"/redirections/${shortId}.qr", "image/png") { ba : Array[Byte] => {
                val is = new ByteArrayInputStream(ba)
                try {
                    ImageIO.read(is);
                } finally {
                    is.close()
                }
            }
        }

        def update(shortId : String, redirection : Redirection) =
            shortDomain.put[Redirection, Redirection](s"/redirections/${shortId}", redirection)

        def remove(shortId : String) = shortDomain.delete(s"/redirections/${shortId}")

        def lookup(evrythngId : Ref) = shortDomain.getPage[Redirection]("/redirections").queryParameter("evrythngId" -> evrythngId.id)

        // TODO add non json
    }

    // context change

    def inProject(projectId : Ref) = new OperatorContext(apiKey, Some(projectId))

}
