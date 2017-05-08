package com.github.jeanadrien.evrythng.scala.rest

import com.github.jeanadrien.evrythng.scala.json.EvtJsonProtocol._
import com.github.jeanadrien.evrythng.scala.json.{Redirection, Time}
import com.typesafe.scalalogging.LazyLogging

/**
  *
  */
class AnonymousRestApi extends
    Environment with LazyLogging {

    override def authorization : Option[String] = None

    val time = new {
        def read = get[Time]("/time")

        def read(tz : String) : EvtGetRequest[Time] = read.queryParameter("tz" -> tz)
    }

    def shortId(s : String) = shortDomain.get[Redirection](s"/$s")
}
