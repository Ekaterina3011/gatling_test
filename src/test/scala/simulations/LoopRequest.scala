package simulations

import io.gatling.core.scenario.Simulation
import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._

class LoopRequest extends Simulation {

  //http conf
  val httpConf = http.baseUrl("https://reqres.in/")
    .header("Accept", "application/json")
    .header("content-type", "application/json")

  def getAllUsersRequest(): ChainBuilder = {
    repeat(2) {
      exec(http("Get all users request")
        .get("api/users?page=2")
        .check(status.in(200 to 201)))
    }
  }

  def getAUsersRequest(): ChainBuilder = {
    repeat(2) {
      exec(http("Get single user request")
        .get("api/users/2")
        .check(status.in(200 to 201)))
    }
  }

  def addUser(): ChainBuilder = {
    repeat(2) {
      exec(http("Add a user request")
        .post("api/users")
        .body(RawFileBody("./src/test/resources/bodies/AddUser.json")).asJson
        .check(status.is(201)))
    }
  }

  //scenario
  val scn = scenario("User request scenario")
    .exec(getAllUsersRequest())
    .pause(2)
    .exec(getAUsersRequest())
    .pause(2)
    .exec(addUser())

  //setup
  setUp(scn.inject(atOnceUsers(1))).protocols(httpConf)
}
