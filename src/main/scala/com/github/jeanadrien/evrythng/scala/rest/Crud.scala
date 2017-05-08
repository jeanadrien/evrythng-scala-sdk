package com.github.jeanadrien.evrythng.scala.rest

import com.github.jeanadrien.evrythng.scala.json.Ref

/**
  *
  */
trait Read[R] extends ResourceContext[R] {
    def read(resourceId : Ref) = env.get[R](s"${urlPath}/${resourceId}")

    def list() = env.getPage[R](urlPath)
}

trait Update[R] extends ResourceContext[R] {

    def update(resourceId : Ref, resource : R) = env.put[R, R](s"${urlPath}/${resourceId}", resource)

}

trait Create[R] extends ResourceContext[R] {

    def create(resource : R) = env.post[R, R](urlPath, resource)

}

trait Delete[R] extends ResourceContext[R] {

    def remove(resourceId : Ref) = env.delete(s"${urlPath}/${resourceId}")

}

trait Crud[R] extends ResourceContext[R] with Create[R] with Read[R] with Update[R] with Delete[R]

trait Cru[R] extends ResourceContext[R] with Create[R] with Read[R] with Update[R]
