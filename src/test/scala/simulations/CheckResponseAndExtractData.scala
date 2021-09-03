package simulations

import io.gatling.core.scenario.Simulation
import io.gatling.core.Predef._
import io.gatling.http.Predef._

class CheckResponseAndExtractData extends Simulation {

  //http conf
  val httpConf = http.baseUrl("https://gorest.co.in/")
    .header("Authorization", "Bearer 6fa1967a900360072d00ab4fbff9b5e60746bf3f4fa530001c8e43a505e8bb94")

  //scenario
  val scn = scenario("check Correlation and extract data")

    //first call - get all the users
    .exec(http("Get all users")
      .get("/public/v1/users")
      .check(jsonPath("$.data[1].id").saveAs("userId")))

    //second api - get a specific user on the basic of id
    .exec(http("Get specific user")
      .get("/public/v1/users/${userId}")
      .check(jsonPath("$.data.id").is("2"))
      .check(jsonPath("$.data.name").is("Kin Varman"))
      .check(jsonPath("$.data.email").is("kin_varman@parker.com")))

  //setup
  setUp(scn.inject(atOnceUsers(1))).protocols(httpConf)

}
