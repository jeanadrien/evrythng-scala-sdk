package com.github.jeanadrien.evrythng.scala.rest

import com.github.jeanadrien.evrythng.scala.json.{Ref, Thng, User, UserStatus}
import org.specs2.concurrent.ExecutionEnv
import org.specs2.specification.BeforeAll

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.util.Random

/**
  *
  */
class UserAuthSpec(implicit val ee : ExecutionEnv) extends TestApplicationContext with BeforeAll {

    override def beforeAll() : Unit = {
        super.beforeAll()
        Await.ready(
            operator.thngs.create(Thng(name = Some("aScoppedThng"))).project(containingProject).exec.map( t =>
                theAppThng = t.copy(properties = None)
            ), 10 seconds
        )
    }

    def is = sequential ^ s2"""
        Using an application apiKey, it is possible to
            create an anonymous user                        $createAnAnonymousUser
            and use it directly                             $checkAnonymousUserKey
            create a regular user                           $createRegularUser
            validate it                                     $validateRegularUser
            log in the user                                 $logInTheUser
            and use it                                      $checkRegularUserKey

        Moreover operator can
            list the users                                  $opListUsers
            see a user                                      $opSeeUser
            update a user                                   $opUpdateUser
            delete a user                                   $opDeleteUser

        User can also
            see itself                                      $userSeeItself
            update itself                                   $userUpdatesItself
            logout                                          $userLogsOut
            and cannot use the former apiKey                $userApiKeyIsInvalid
    """

    var theAppThng : Thng = null
    var theAnonymousUser : UserStatus = null
    var theRegularUser : UserStatus = null
    var theRegularUserObject : User = null

    val regUserLogin = s"${Random.alphanumeric.take(10).mkString}@test.nw"
    val regUserPassword = "abcd1234A$&"
    var regUserActivationCode : String = null

    val randomFirstName = Random.nextString(10)

    def createAnAnonymousUser = applicationApi.auth.evrythng.users.createAnonymous().exec.map { userStatus =>
        theAnonymousUser = userStatus
        userStatus.evrythngUser
    } must beSome[Ref].await

    def checkUserKey(userKey : String) = env.userApi(userKey).thngs.list().exec.map(
        _.items.map(_.copy(properties = None))
    ) must contain(
        theAppThng
    ).await

    def checkAnonymousUserKey = checkUserKey(theAnonymousUser.evrythngApiKey.get)
    def checkRegularUserKey = checkUserKey(theRegularUser.evrythngApiKey.get)

    def createRegularUser = applicationApi.auth.evrythng.users.create(
        User(
            firstName = Some(randomFirstName),
            lastName = Some(Random.nextString(10)),
            email = Some(regUserLogin),
            password = Some(regUserPassword)
        )
    ).exec.map { userStatus =>
        regUserActivationCode = userStatus.activationCode.get
        theRegularUser = userStatus
        userStatus.evrythngUser
    } must beSome[Ref].await

    def validateRegularUser = applicationApi.auth.evrythng.users(theRegularUser.evrythngUser.get).
        validate(regUserActivationCode).exec.map { validated =>
        theRegularUser = validated
        validated.evrythngApiKey
    } must beSome[String].await

    def logInTheUser =
        applicationApi.auth.evrythng.login(regUserLogin, regUserPassword).exec.map(
            _.copy(email = None)
        ) must beEqualTo(theRegularUser).await

    def opListUsers = operator.inProject(containingProject.id.get).users.list.exec.map(_.items.size) must beEqualTo(1).await

    def opSeeUser = operator.users.read(theRegularUser.evrythngUser.get).exec.map { user =>
        theRegularUserObject = user
        user.firstName.get
    } must beEqualTo(randomFirstName).await

    def opUpdateUser = {
        val newLastName = Random.nextString(20)
        operator.users.update(theRegularUser.evrythngUser.get, User(lastName = Some(newLastName))).exec
            .map { updated =>
                theRegularUserObject = updated
                updated.lastName.get
            } must beEqualTo(newLastName).await
    }

    def opDeleteUser = operator.users.remove(theAnonymousUser.evrythngUser.get).exec.map(_ => ()) must beEqualTo(()).await

    def userSeeItself = env.userApi(theRegularUser).users.read.exec must beEqualTo(theRegularUserObject).await

    def userUpdatesItself = {
        val newLastName = Random.nextString(20)
        env.userApi(theRegularUser).users.update(
            User(lastName = Some(newLastName))
        ).exec.map { update =>
            theRegularUserObject = update
            update.lastName.get
        } must beEqualTo(newLastName).await
    }

    def userLogsOut = env.userApi(theRegularUser).auth.all.logout.exec must beEqualTo(()).await

    def userApiKeyIsInvalid = env.userApi(theRegularUser).thngs.list().exec must throwAn[EvtRequestException].like {
        case e : EvtRequestException => e.evtError.status must beEqualTo(403)
    }.await
}
