package com.github.jeanadrien.evrythng.scala.rest

import com.github.jeanadrien.evrythng.scala.json.EvtJsonProtocol._
import com.github.jeanadrien.evrythng.scala.json.{Location, Ref}
import com.typesafe.scalalogging.LazyLogging

/**
  *
  */
class ThngContext(thngId : Ref, val apiKey : String, val projectScope : Option[Ref] = None) extends
    Environment with AuthorizedEnvironment with LazyLogging with ContextWithProperties {

    override def defaultQueryParams : Seq[(String, String)] = super
        .defaultQueryParams ++ projectScope.map("project" -> _.toString)

    val actions = new ContextWithActions {

        override def apply(
            actionType : String
        ) : ActionContext = new ActionContext(s"/thngs/${thngId}", actionType, apiKey, projectScope)
    }

    val location = new {

        def create(
            locations : List[Location]
        ) = put[List[Location], List[Location]](s"/thngs/${thngId}/location", locations)

        def list = getPage[Location](s"/thngs/${thngId}/location")

        def remove(untilTimestamp : Option[Long]) = (delete(s"/thngs/${thngId}/location") /: untilTimestamp) { (b, s) =>
            b.queryParameter("to" -> s.toString)
        }
    }

    override def contextRoot : String = s"/thngs/${thngId}"

}
