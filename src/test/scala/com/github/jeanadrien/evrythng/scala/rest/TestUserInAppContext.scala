package com.github.jeanadrien.evrythng.scala.rest

import org.specs2.concurrent.ExecutionEnv
import org.specs2.specification.BeforeAll

import scala.concurrent.Await
import scala.concurrent.duration._

/**
  *
  */
abstract class TestUserInAppContext extends TestApplicationContext with BeforeAll {

    implicit val ee : ExecutionEnv

    var userApi : UserRestApi = null

    override def beforeAll() : Unit = {
        super.beforeAll()
        Await.ready(
            applicationApi.auth.evrythng.users.createAnonymous().exec map { userAuth =>
                userApi = env.userApi(userAuth)
            }, 10 seconds
        )
    }

}
