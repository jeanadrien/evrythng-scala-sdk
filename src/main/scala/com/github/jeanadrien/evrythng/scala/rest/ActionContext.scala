package com.github.jeanadrien.evrythng.scala.rest

import com.github.jeanadrien.evrythng.scala.json.EvtJsonProtocol._
import com.github.jeanadrien.evrythng.scala.json.{Action, Ref}
import com.typesafe.scalalogging.LazyLogging

/**
  *
  */
class ActionContext(private[rest] val actionRoot : String, actionType : String, val apiKey : String, projectScope : Option[Ref] = None) extends
    Environment with AuthorizedEnvironment with LazyLogging {

    override def defaultQueryParams : Seq[(String, String)] = super
        .defaultQueryParams ++ projectScope.map("project" -> _.toString)

    def list = getPage[Action](s"${actionRoot}/actions/${actionType}")

    def read(actionId : Ref) = get[Action](s"${actionRoot}/actions/${actionType}/${actionId}")

    def create(action : Action) = post[Action, Action](s"${actionRoot}/actions/${actionType}", action.copy(`type` = Some(actionType)))

    def remove(actionId : Ref) = delete(s"${actionRoot}/actions/${actionType}/${actionId}")

}
