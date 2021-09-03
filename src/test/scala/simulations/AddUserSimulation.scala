package simulations

import io.gatling.core.scenario.Simulation
import io.gatling.core.Predef._
import io.gatling.http.Predef._

class AddUserSimulation extends Simulation {

  //http conf
  val httpConf = http.baseUrl("https://reqres.in/")
    .header("Accept", value = "application/json")
    .header("content-type", value = "application/json")

  //scenario
  val scn = scenario("Add User Scenario")
    .exec(http("/api/users")
      .post("/api/users")
      .body(RawFileBody("./src/test/resources/bodies/AddUser.json")).asJson
      .header("content-type", value = "application/json")
      .check(status is 201))

    .pause(3)

    .exec(http("get user request")
      .get("/api/users/2")
      .check(status is 200))

    .pause(2)

    .exec(http("get all users request")
      .get("/api/users?page=2")
      .check(status is 200))

  //setup
  setUp(scn.inject(atOnceUsers(2)).protocols(httpConf)) //concurrent users

}
