package com.github.jeanadrien.evrythng.scala.rest

import java.net.URLDecoder

import com.github.jeanadrien.evrythng.scala.config.EvrythngSdkSettings
import com.github.jeanadrien.evrythng.scala.json.EvtJsonProtocol._
import com.github.jeanadrien.evrythng.scala.json.{EvtError, Project, Ref}
import com.typesafe.scalalogging.LazyLogging
import spray.json._

import scala.concurrent.{ExecutionContext, Future}

/**
  *
  */
abstract class EvtRequest(url : String, apiKey : Option[String]) extends LazyLogging {

    // TODO I'll would be happy to find another way
    type RequestType

    implicit val executionContext = EvtRequest.executionContext

    var queryString = Map[String, String]()

    protected def build =
        EvtRequest.httpRestClient.buildRequest(url,
            Seq("Accept" -> "application/json",
                "Content-Type" -> "application/json") ++
                apiKey.toSeq.map("Authorization" -> _),
            queryString.toSeq)

    protected val responseLog = (restResponse : HttpRestResponse) =>
        s"${restResponse.status} ${restResponse.statusText} ${restResponse.body}"

    protected def accept(expectedStatusCode : Int*) : HttpRestResponse => HttpRestResponse = { restResponse =>
        logger.debug(s"<<< Response ${responseLog(restResponse)}")
        if (!expectedStatusCode.contains(restResponse.status)) {
            try {
                val json = restResponse.body.parseJson
                val evtError = json.convertTo[EvtError]
                throw new EvtRequestException(evtError)
            } catch {
                case e : DeserializationException =>
                    throw new EvtRequestException(EvtError(
                        restResponse.status,
                        restResponse.body :: Nil,
                        None,
                        None
                    ))
                case ee : Exception =>
                    throw ee
            }
        } else {
            restResponse
        }
    }

    def queryParameter(params : (String, String)*) : RequestType = {
        queryString = queryString ++ params
        this.asInstanceOf[RequestType]
    }

    def perPage(perPage : Int) = queryParameter("perPage" -> perPage.toString)

    def project(project : Ref) = queryParameter("project" -> project.id)

    def project(projectObj : Project) : RequestType = project(projectObj.id.get)

    override def toString : String = {
        val qs = if (queryString.isEmpty) "" else "?" + queryString.toSeq.map { case (a, b) => s"${a}=${b}" }
            .mkString("&")
        val key = apiKey.map(s => s" [Authorization: ${mask(s)}]").getOrElse("")
        s"${url}${qs}${key}"
    }

    private def mask(str : String) = if (str.length > 10)
        s"${str.take(5)}...${str.takeRight(5)}"
    else str

}

class EvtGetRequest[T](url : String, apiKey : Option[String])(implicit val reader : JsonReader[T]) extends
    EvtRequest(url, apiKey) {

    type RequestType = EvtGetRequest[T]

    def exec : Future[T] = build.get().map(accept(200)).map { wsResponse =>
        logger.debug(s">>> GET ${this.toString}")
        val json = wsResponse.body.parseJson
        json.convertTo[T]
    }
}

class EvtGetDataRequest[T](url : String, apiKey : Option[String], accept : String, builder : Array[Byte] => T)
    extends EvtRequest(url, apiKey) {

    type RequestType = EvtGetDataRequest[T]

    override protected def build =
        EvtRequest.httpRestClient.buildRequest(url,
            Seq("Accept" -> accept,
                "Content-Type" -> "application/json") ++
                apiKey.toSeq.map("Authorization" -> _),
            queryString.toSeq)

    override protected val responseLog = (restResponse : HttpRestResponse) =>
        s"${restResponse.status} ${restResponse.statusText} (${restResponse.bodyAsBytes.size} bytes)"

    def exec : Future[T] = {
        build.get().map(accept(200)).map { wsResponse =>
            logger.debug(s">>> GET DATA ($accept) ${this.toString}")
            builder(wsResponse.bodyAsBytes)
        }
    }
}

class EvtPostRequest[I, O](body : I, url : String, apiKey : Option[String])
    (implicit val writer : JsonWriter[I], implicit val reader : JsonReader[O]) extends
    EvtRequest(url, apiKey) {

    type RequestType = EvtPostRequest[I, O]

    def exec : Future[O] = {
        logger.debug(s">>> POST ${this.toString}")
        logger.trace(s" -> ${body.toJson.prettyPrint}")
        val jsonBody = body.toJson.compactPrint
        build.post(jsonBody).map(accept(200, 201, 204)).map { wsResponse =>
            val json = wsResponse.body.parseJson
            json.convertTo[O]
        }
    }
}

class EvtPostAndForgetRequest[I](body : I, url : String, apiKey : Option[String])
    (implicit val writer : JsonWriter[I]) extends
    EvtRequest(url, apiKey) {

    type RequestType = EvtPostAndForgetRequest[I]

    def exec : Future[Unit] = {
        logger.debug(s">>> POST ${this.toString} (and forget)")
        val jsonBody = body.toJson.compactPrint
        build.post(jsonBody).map(accept(200, 201, 204)).map { _ =>
            ()
        }
    }
}

class EvtPutRequest[I, O](body : I, url : String, apiKey : Option[String])
    (implicit val writer : JsonWriter[I], implicit val reader : JsonReader[O]) extends
    EvtRequest(url, apiKey) {

    type RequestType = EvtPutRequest[I, O]

    def exec : Future[O] = {
        logger.debug(s">>> PUT ${this.toString}")
        val jsonBody = body.toJson.compactPrint
        build.put(jsonBody).map(accept(200)).map { wsResponse =>
            val json = wsResponse.body.parseJson
            json.convertTo[O]
        }
    }
}

class EvtDeleteRequest(url : String, apiKey : Option[String]) extends
    EvtRequest(url, apiKey) {

    type RequestType = EvtDeleteRequest

    def exec() : Future[Unit] = {
        logger.debug(s">>> DELETE ${this.toString}")
        build.delete().map(accept(200)).map { _ => () }
    }
}

class EvtGetPageRequest[T](url : String, apiKey : Option[String])(implicit val reader : JsonReader[T]) extends
    EvtRequest(url, apiKey) {

    type RequestType = EvtGetPageRequest[T]

    def exec : Future[Page[T]] = {
        logger.debug(s">>> GET ${this.toString} (page)")
        build.get().map(accept(200)).map { wsResponse =>
            val json = wsResponse.body.parseJson.asInstanceOf[JsArray]
            val items = json.elements.map(_.convertTo[T])
            val next = wsResponse.header("link").flatMap {
                case EvtRequest.LinkExpression(link) =>
                    Some(URLDecoder.decode(link))
                case _ =>
                    None
            }
            new Page(items, next, reader)
        }
    }
}

object EvtRequest extends LazyLogging with EvrythngSdkSettings {
    // simple injection mechanism
    val restClientImpl = settings.sdk.httpClient
    logger.info(s"Use httpRestClient: ${restClientImpl}")
    lazy val httpRestClient : HttpRestClient = Class.forName(restClientImpl).newInstance().asInstanceOf[HttpRestClient]

    var executionContext : ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global
    val LinkExpression = """<([^>]*)>;.*""".r
}
