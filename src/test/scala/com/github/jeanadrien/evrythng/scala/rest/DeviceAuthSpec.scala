package com.github.jeanadrien.evrythng.scala.rest

import com.github.jeanadrien.evrythng.scala.json._
import org.specs2.concurrent.ExecutionEnv
import org.specs2.specification.BeforeAll
import spray.json.JsString

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.util.Random

/**
  *
  */
class DeviceAuthSpec(implicit val ee : ExecutionEnv) extends TestUserInAppContext with BeforeAll {

    var deviceThng : Thng = null

    override def beforeAll() : Unit = {
        super.beforeAll()

        Await.ready(
            operator.inProject(containingProject.id.get).thngs.create(
                Thng(name = Some(Random.nextString(20)))
            ).exec.map(deviceThng = _),
            5 seconds
        )
    }

    def is = sequential ^ s2"""
        Using a user apiKey, it is possible to
            create device key for a visible thng        $createDeviceKey
            read the device key                         $readTheDeviceKey

        Then using this device key, it is possible
            to read the device                          $deviceReadsItself
            to update the device                        $deviceUpdatesItself
            to generate some properties                 $deviceCreatesProps
            and see them                                $deviceReadsProps
            to generate some locations                  $deviceCreatesLocation
            and see them                                $deviceReadsLocation
            to generate some actions                    $deviceCreatesAction
            and see them                                $deviceReadsAction

        The user can then
            delete (deauth) the device                  $userDeauthDevice
            miss the key                                $userMissDevice

        And the previous device key
            is not valid anymore                        $deviceKeyIsInvalid
    """

    var theDeviceAuth : DeviceAuth = null
    var deviceApi : DeviceRestApi = null

    val normThngComparison = (t : Thng) => t.copy(properties = None, location = None, updatedAt = None)

    def createDeviceKey = userApi.auth.evrythng.thngs.create(deviceThng.id.get).exec.map { auth =>
        theDeviceAuth = auth
        deviceApi = env.deviceApi(auth)
        auth.thngId
    } must beEqualTo(deviceThng.id).await

    def readTheDeviceKey =
        userApi.auth.evrythng.thngs.read(deviceThng.id.get).exec must beEqualTo(theDeviceAuth).await

    def deviceReadsItself =
        deviceApi.thngs.read.exec.map(normThngComparison) must beEqualTo(
            normThngComparison(deviceThng)
        ).await

    def deviceUpdatesItself = {
        val newRandomName = Random.nextString(20)
        deviceApi.thngs.update(Thng(
            name = Some(newRandomName)
        )).exec.map { updated =>
            deviceThng = updated
            updated.name.get
        } must beEqualTo(newRandomName).await
    }

    var prop = Property(
        key = Some("randomkey"),
        value = JsString(Random.alphanumeric.take(20).mkString)
    )

    def deviceCreatesProps = deviceApi.thng.properties.create(prop::Nil).exec.map(
        _.map(_.copy(timestamp = None))
    ) must containTheSameElementsAs(prop::Nil).await

    def deviceReadsProps = deviceApi.thng.properties(prop.key.get).list.exec.map(
        _.items.map(_.copy(timestamp = None, key = prop.key))
    ) must containTheSameElementsAs(prop::Nil).await

    var location = Location(Some(23.5), Some(12.3))

    def deviceCreatesLocation = deviceApi.thng.location.create(location::Nil).exec.map(
        _.map(_.copy(timestamp = None))
    ) must containTheSameElementsAs(location::Nil).await

    def deviceReadsLocation = deviceApi.thng.location.list.exec.map(
        _.items.map(_.copy(timestamp = None))
    ) must containTheSameElementsAs(location::Nil).await

    var scanAction : Action = null;
    val normActionComparison = (a : Action) => a.copy(
        location = None,
        locationSource = None,
        context = None
    )

    def deviceCreatesAction = deviceApi.thng.actions("scans").create(Action()).exec.map { action =>
        scanAction = action
        action.thng.get
    } must beEqualTo(deviceThng.id.get).await

    def deviceReadsAction = deviceApi.thng.actions("scans").list.exec.map(
        _.items.map(normActionComparison)
    ) must containTheSameElementsAs(normActionComparison(scanAction)::Nil).await

    def userDeauthDevice =
        userApi.auth.evrythng.thngs.remove(deviceThng.id.get).exec map(_ => ()) must beEqualTo(()).await

    def userMissDevice =
        userApi.auth.evrythng.thngs.read(deviceThng.id.get).exec must throwAn[EvtRequestException].like {
            case e : EvtRequestException => e.evtError.status must beEqualTo(404)
        }.await

    def deviceKeyIsInvalid =
        deviceApi.thngs.read.exec must throwAn[EvtRequestException].like {
            case e : EvtRequestException => e.evtError.status must beEqualTo(403)
        }.await
}
