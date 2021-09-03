package simulations

import io.gatling.core.scenario.Simulation
import io.gatling.core.Predef._
import io.gatling.http.Predef._

class DataFeederCsv extends Simulation {

  //http conf
  val httpConf = http.baseUrl("https://gorest.co.in/")
    .header("Authorization", "Bearer 6fa1967a900360072d00ab4fbff9b5e60746bf3f4fa530001c8e43a505e8bb94")

  //circular,shuffle, random, queue
  val csvFeeder = csv("./src/test/resources/data/getUser.csv").circular

  def getUser() = {
    repeat(7) {
      feed(csvFeeder)
        .exec(http("Get single user request")
          .get("public/v1/users/${userId}")
          .check(jsonPath("$.data.name").is("${name}"))
          .check(status.in(200, 304)))
        .pause(2)
    }
  }

  //scenario
  val scn = scenario("CSV FEEDER test").exec(getUser())

  //setup
  setUp(scn.inject(atOnceUsers(1))).protocols(httpConf)
}
