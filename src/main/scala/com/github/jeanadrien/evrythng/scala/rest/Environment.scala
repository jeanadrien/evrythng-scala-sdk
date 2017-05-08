package com.github.jeanadrien.evrythng.scala.rest

import java.net.URI

import com.github.jeanadrien.evrythng.scala.config.EvrythngSdkSettings
import com.github.jeanadrien.evrythng.scala.json._


/**
  *
  */
class Environment extends EvrythngSdkSettings with EvtRequestBuilder {

    parent =>

    val urlBase = new URI(settings.evrythng.api)

    def authorization : Option[String] = None

    def defaultQueryParams : Seq[(String, String)] = Seq()


    def shortDomain = new EvtRequestBuilder {
        override val urlBase = new URI(settings.evrythng.shortDomain)

        override def authorization : Option[String] = parent.authorization

        override def defaultQueryParams : Seq[(String, String)] = parent.defaultQueryParams
    }

    // structured actor APIs

    def operatorApi(access : AccountAccess) : OperatorContext = operatorApi(access.apiKey.get)

    def operatorApi(operatorKey : String) = new OperatorContext(operatorKey)

    def applicationApi(applicationApiKey : String) = new ApplicationRestApi(applicationApiKey)

    def applicationApi(me : Application) : ApplicationRestApi = applicationApi(me.appApiKey.get)

    def userApi(userApiKey : String) = new UserRestApi(userApiKey)

    def userApi(me : UserStatus) = new UserRestApi(me.evrythngApiKey.get) with SelfUser {
        override val userId : Ref = me.evrythngUser.get
    }

    def deviceApi(deviceApiKey : String, thngId : Ref) = new DeviceRestApi(deviceApiKey, thngId)

    def deviceApi(auth : DeviceAuth) : DeviceRestApi = deviceApi(auth.thngApiKey.get, auth.thngId.get)

    def trustedApplicationApi(trustedApiKey : String) = new ApplicationTrustedRestApi(trustedApiKey)

    def trustedApplicationApi(me : SecretKey) : ApplicationTrustedRestApi = trustedApplicationApi(me.secretApiKey)

    def anonymousApi = new AnonymousRestApi()

}

object Environment extends Environment
