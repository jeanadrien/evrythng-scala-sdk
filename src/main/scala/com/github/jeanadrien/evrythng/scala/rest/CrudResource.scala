package com.github.jeanadrien.evrythng.scala.rest

import com.github.jeanadrien.evrythng.scala.json.Ref
import spray.json.JsonFormat

/**
  *
  */
trait CrudResource[R] {

    // TODO remove this trait

    val urlPath : String
    val env : Environment
    implicit val format : JsonFormat[R]

    def read(resourceId : Ref) = env.get[R](s"${urlPath}/${resourceId}")

    def update(resourceId : Ref, resource : R) = env.put[R, R](s"${urlPath}/${resourceId}", resource)

    def create(resource : R) = env.post[R, R](urlPath, resource)

    // TODO probably rename to remove
    def delete(resourceId : Ref) = env.delete(s"${urlPath}/${resourceId}")

    def list() = env.getPage[R](urlPath)
}
