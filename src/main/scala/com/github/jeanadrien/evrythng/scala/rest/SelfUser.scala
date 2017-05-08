package com.github.jeanadrien.evrythng.scala.rest

import com.github.jeanadrien.evrythng.scala.json.EvtJsonProtocol._
import com.github.jeanadrien.evrythng.scala.json.{Ref, User}

/**
  *
  */
trait SelfUser {

    this : Environment =>

    val userId : Ref

    val users = new {

        def read = get[User](s"/users/${userId}")

        def update(user : User) = put[User, User](s"/users/${userId}", user)

    }
}
