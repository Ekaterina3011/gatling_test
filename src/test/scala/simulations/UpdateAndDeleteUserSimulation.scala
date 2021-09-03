package simulations

import io.gatling.core.scenario.Simulation
import io.gatling.core.Predef._
import io.gatling.http.Predef._

class UpdateAndDeleteUserSimulation extends Simulation {

  //http conf
  val httpConf = http.baseUrl("https://reqres.in/")
    .header("Accept", value = "application/json")
    .header("content-type", value = "application/json")

  //scenario
  val scn = scenario("Update User scenario")

    //first updating the user
    .exec(http("Update specific user")
      .put("api/users/2")
      .body(RawFileBody("./src/test/resources/bodies/UpdateUser.json")).asJson
      .check(status.in(200 to 201)))

    .pause(3)

    //second - deleting the user
    .exec(http("delete user")
      .delete("api/users/2")
      .check(status.is(204)))

  //setup
  setUp(scn.inject(atOnceUsers(1))).protocols(httpConf)
}
