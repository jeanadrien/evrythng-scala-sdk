package com.github.jeanadrien.evrythng.scala.rest

import com.github.jeanadrien.evrythng.scala.json.EvtJsonProtocol._
import com.github.jeanadrien.evrythng.scala.json.Property

/**
  *
  */
trait ContextWithProperties extends RestContext {

    // TODO : Alternative logical: class PropertiesContext which define the context root as a class member
    // TODO : Why RestContext, why not AuthorizedEnv

    this : Environment =>

    def contextRoot : String

    val properties = new {

        def list = getPage[Property](s"${contextRoot}/properties")

        def create(
            properties : List[Property]
        ) = put[List[Property], List[Property]](s"${contextRoot}/properties", properties)

        def remove = delete(s"${contextRoot}/properties")

        def apply(key : String) = new PropertyContext(contextRoot, key, authorization.get, projectScope)
    }

}
