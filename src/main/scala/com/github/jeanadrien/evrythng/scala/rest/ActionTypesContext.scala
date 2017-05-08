package com.github.jeanadrien.evrythng.scala.rest

import com.github.jeanadrien.evrythng.scala.json.EvtJsonProtocol._
import com.github.jeanadrien.evrythng.scala.json.{ActionType, Ref}
import com.typesafe.scalalogging.LazyLogging

/**
  * /actions
  */
class ActionTypesContext(val apiKey : String, projectScope : Option[Ref] = None) extends
Environment with AuthorizedEnvironment with LazyLogging with ContextWithActions {

    override def defaultQueryParams : Seq[(String, String)] = super
    .defaultQueryParams ++ projectScope.map("project" -> _.toString)

    def list = getPage[ActionType]("/actions")

    def create(actionType : ActionType) = post[ActionType, ActionType]("/actions", actionType)

    // TODO normalize naming
    def remove(actionType : String) = delete(s"/actions/${actionType}")

    def apply(actionType : String) = new ActionContext("", actionType, apiKey, projectScope)

}
