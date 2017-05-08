package com.github.jeanadrien.evrythng.scala.rest

import com.github.jeanadrien.evrythng.scala.json.SecretKey
import org.specs2.concurrent.ExecutionEnv
import org.specs2.specification.BeforeAll

/**
  *
  */
class TrustedApplicationRestApiSpec(implicit val ee : ExecutionEnv)
    extends TestApplicationContext with BeforeAll {

    var theSecretKey : SecretKey = null

    def is = sequential ^ s2"""
        The Trusted Application Rest ApiKey (no shit)
            can be requested by an operator     $readSecretKey
            and used to perform requests        $readSelf
    """

    def readSecretKey =
        operator.project(containingProject.id.get).application(application.id.get).secretKey.read.exec.map { key =>
            theSecretKey = key
            key.secretApiKey.length
        } must beEqualTo(80).await

    def readSelf =
        env.trustedApplicationApi(theSecretKey).me.read.exec must beEqualTo(application).await

}
