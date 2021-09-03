package simulations

import io.gatling.core.scenario.Simulation
import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._
import scala.concurrent.duration._

class FixedDurationLoadSimulation extends Simulation {

  //http conf
  val httpConf = http.proxy(Proxy("localhost", 8888))
    .baseUrl("https://reqres.in/")
    .header("Accept", "application/json")
    .header("content-type", "application/json")

  def getAllUsersRequest(): ChainBuilder = {
    repeat(2) {
      exec(http("Get all users request")
        .get("api/users?page=2")
        .check(status.is(200)))
    }
  }

  def getAUsersRequest() = {
    repeat(2) {
      exec(http("Get single user request")
        .get("api/users/2")
        .check(status.is(200)))
    }
  }

  def addAUser() = {
    repeat(2) {
      exec(http("Add a user request")
        .post("api/users")
        .body(RawFileBody("./src/test/resources/bodies/AddUser.json")).asJson
        .check(status.in(200 to 201)))
    }
  }

  //scenario
  val scn = scenario("Fixed duration load simulation")
    .forever()(
      exec(getAllUsersRequest())
      .pause(2)
      .exec(getAUsersRequest())
      .pause(2)
      .exec(addAUser()))

  setUp(
    scn.inject(
      nothingFor(5),
      atOnceUsers(10), //начиная с 10
      rampUsers(50).during(30.seconds)
    ).protocols(httpConf)
  ).maxDuration(1.minute)
}
