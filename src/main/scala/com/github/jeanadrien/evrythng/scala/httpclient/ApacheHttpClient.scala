package com.github.jeanadrien.evrythng.scala.httpclient

import java.net.URI

import com.github.jeanadrien.evrythng.scala.rest.{HttpRestClient, HttpRestRequest, HttpRestResponse}
import com.typesafe.scalalogging.LazyLogging
import org.apache.http.Header
import org.apache.http.client.methods._
import org.apache.http.client.utils.URIBuilder
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.{CloseableHttpClient, HttpClients}
import org.apache.http.message.BasicHeader
import org.apache.http.util.EntityUtils

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  *
  */
class ApacheHttpClient extends HttpRestClient with LazyLogging {
    lazy val httpClient : CloseableHttpClient = HttpClients.createDefault()

    implicit def toHttpResponse(apacheResponse : CloseableHttpResponse) : HttpRestResponse = try {
        new HttpRestResponse {
            override val statusText : String = apacheResponse.getStatusLine.getReasonPhrase

            override val bodyAsBytes : Array[Byte] = EntityUtils.toByteArray(apacheResponse.getEntity)

            val headers = apacheResponse.getAllHeaders.toList

            override def header(
                name : String
            ) : Option[String] = headers.find(_.getName == name).map(_.getValue)

            override val status : Int = apacheResponse.getStatusLine.getStatusCode
        }
    } finally {
        apacheResponse.close()
    }

    override def shutDown: Unit = {
        httpClient.close()
    }

    override def buildRequest(
        url : String,
        headers         : Seq[(String, String)],
        queryParameters : Seq[(String, String)]
    ) : HttpRestRequest = new HttpRestRequest {

        val uri : URI = queryParameters.foldLeft(new URIBuilder(url))((builder, pair) => {
            val (key, value) = pair
            builder.setParameter(key, value)
        }).build()

        logger.debug(s"ApacheHttpClient Build URL: ${uri}")

        val apacheHeaders : Array[Header] =
            headers.map { case (k, v) => new BasicHeader(k, v) } toArray

        override def get() : Future[HttpRestResponse] = {
            val httpGet = new HttpGet(uri)
            httpGet.setHeaders(apacheHeaders)

            Future {
                httpClient.execute(httpGet)
            }
        }

        override def put(body : String) : Future[HttpRestResponse] = {
            val httpPut = new HttpPut(uri)
            httpPut.setHeaders(apacheHeaders)
            httpPut.setEntity(new StringEntity(body, "UTF-8"))

            Future {
                httpClient.execute(httpPut)
            }
        }

        override def delete() : Future[HttpRestResponse] = {
            val httpDelete = new HttpDelete(uri)
            httpDelete.setHeaders(apacheHeaders)

            Future {
                httpClient.execute(httpDelete)
            }
        }

        override def post(
            body : String
        ) : Future[HttpRestResponse] = {
            val httpPost = new HttpPost(uri)
            httpPost.setHeaders(apacheHeaders)
            httpPost.setEntity(new StringEntity(body, "UTF-8"))

            Future {
                httpClient.execute(httpPost)
            }
        }
    }
}
