package com.github.jeanadrien.evrythng.scala.httpclient

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.github.jeanadrien.evrythng.scala.rest.{HttpRestClient, HttpRestRequest, HttpRestResponse}
import play.api.libs.ws.WSResponse
import play.api.libs.ws.ahc.{AhcWSClient, AhcWSClientConfig}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Failure

/**
  *
  */
class PlayHttpClient extends HttpRestClient {
    implicit val system = ActorSystem()
    implicit val materializer = ActorMaterializer()

    val config = AhcWSClientConfig(maxRequestRetry = 5)
    val wsClient = AhcWSClient(config)

    implicit def toHttpRestResponse(wsResponse : WSResponse) = new HttpRestResponse {

        override def statusText : String = wsResponse.statusText

        override def bodyAsBytes : Array[Byte] = wsResponse.bodyAsBytes.toArray

        override def header(name : String) : Option[String] = wsResponse.header(name)

        override def status : Int = wsResponse.status
    }

    override def buildRequest(
        url             : String,
        headers         : Seq[(String, String)],
        queryParameters : Seq[(String, String)]
    ) : HttpRestRequest = new HttpRestRequest {

        val req = wsClient.
            url(url).
            withHeaders(headers : _*).
            withQueryString(queryParameters : _*)

        override def get(): Future[HttpRestResponse] = req.get().map(toHttpRestResponse)
        override def put(body: String): Future[HttpRestResponse] = req.put(body).map(toHttpRestResponse)
        override def delete(): Future[HttpRestResponse] = req.delete().map(toHttpRestResponse)

        override def post(
            body : String
        ) : Future[HttpRestResponse] =  req.post(body).map(toHttpRestResponse)

    }

    def shutDown(): Unit = {
        wsClient.close()
        system.terminate().onComplete {
            case Failure(t) =>
                sys.error("Failed to terminate")
            case _ => ()
        }
    }
}
