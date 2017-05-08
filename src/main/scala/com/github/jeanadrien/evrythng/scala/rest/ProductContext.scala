package com.github.jeanadrien.evrythng.scala.rest

import com.github.jeanadrien.evrythng.scala.json.Ref
import com.typesafe.scalalogging.LazyLogging

/**
  *
  */
class ProductContext(productId : Ref, val apiKey : String, val projectScope : Option[Ref] = None) extends
    Environment with AuthorizedEnvironment with LazyLogging with ContextWithProperties {

    override def defaultQueryParams : Seq[(String, String)] = super
        .defaultQueryParams ++ projectScope.map("project" -> _.toString)

    val actions = new ContextWithActions {

        override def apply(
            actionType : String
        ) : ActionContext = new ActionContext(s"/products/${productId}", actionType, apiKey, projectScope)
    }

    override def contextRoot : String = s"/products/${productId}"

}
