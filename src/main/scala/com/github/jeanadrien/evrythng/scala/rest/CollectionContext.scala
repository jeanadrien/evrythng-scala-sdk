package com.github.jeanadrien.evrythng.scala.rest

import com.github.jeanadrien.evrythng.scala.json.EvtJsonProtocol._
import com.github.jeanadrien.evrythng.scala.json.{Collection, Ref, Thng}
import com.typesafe.scalalogging.LazyLogging

/**
  *
  */
class CollectionContext(collectionId: Ref, val apiKey : String, projectScope : Option[Ref] = None) extends
    Environment with AuthorizedEnvironment with LazyLogging {

    self =>

    override def defaultQueryParams : Seq[(String, String)] = super
        .defaultQueryParams ++ projectScope.map("project" -> _.toString)

    object thngs {

        def read = self.getPage[Thng](s"/collections/${collectionId}/thngs")

        def add(thngIds : List[Ref]) : EvtPutRequest[List[Ref], Collection] =
            self.put[List[Ref], Collection](s"/collections/${collectionId}/thngs", thngIds)

        def remove(thngId : Ref) = self.delete(s"/collections/${collectionId}/thngs/${thngId}")

        def removeAll() = self.delete(s"/collections/${collectionId}/thngs")

    }

    object collections {

        def read = self.getPage[Collection](s"/collections/${collectionId}/collections")

        def add(collectionIds : List[Ref]) =
            self.postAndForget[List[Ref]](s"/collections/${collectionId}/collections", collectionIds)

        def remove(toRemoveCollectionId : Ref) = self.delete(s"/collections/${collectionId}/collections/${toRemoveCollectionId}")

        def removeAll() = self.delete(s"/collections/${collectionId}/collections")

    }

    val actions = new ContextWithActions {

        override def apply(
            actionType : String
        ) : ActionContext =  new ActionContext(s"/collections/${collectionId}", actionType, apiKey, projectScope)
    }
}
