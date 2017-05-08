package com.github.jeanadrien.evrythng.scala.rest

import java.net.URI

import spray.json.{JsonReader, JsonWriter}

/**
  *
  */
trait EvtRequestBuilder {

    def urlBase : URI

    def authorization : Option[String]

    def defaultQueryParams : Seq[(String, String)]

    private def absUrl(url : String) = urlBase.resolve(url).normalize().toASCIIString()

    def get[T](url : String)(implicit reader : JsonReader[T]) = new EvtGetRequest[T](absUrl(url), authorization).queryParameter(defaultQueryParams : _*)
    def post[I, O](url : String, body : I)(implicit writer : JsonWriter[I], reader : JsonReader[O])
    = new EvtPostRequest[I, O](body, absUrl(url), authorization).queryParameter(defaultQueryParams : _*)
    def postAndForget[I](url : String, body : I)(implicit writer : JsonWriter[I])
    = new EvtPostAndForgetRequest[I](body, absUrl(url), authorization).queryParameter(defaultQueryParams : _*)
    def put[I, O](url : String, body : I)(implicit writer : JsonWriter[I], reader : JsonReader[O])
    = new EvtPutRequest[I, O](body, absUrl(url), authorization).queryParameter(defaultQueryParams : _*)
    def delete(url : String) = new EvtDeleteRequest(absUrl(url), authorization).queryParameter(defaultQueryParams : _*)

    def getData[T](url: String, mime : String)(builder : Array[Byte] => T) =
        new EvtGetDataRequest(absUrl(url), authorization, mime, builder).queryParameter(defaultQueryParams : _*)

    def getPage[T](url : String)(implicit reader : JsonReader[T]) = new EvtGetPageRequest[T](absUrl(url), authorization).queryParameter(defaultQueryParams : _*)

    def nextPage[T](page : Page[T]) = page.nextPageUrl.map(url => new EvtGetPageRequest[T](url, authorization)(page.reader))

}
