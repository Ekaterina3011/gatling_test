package simulations

import io.gatling.core.scenario.Simulation
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class LoadSimulationBasic extends Simulation {

  val httpConf = http.baseUrl("https://gorest.co.in/")
    .header("Authorization", "Bearer 6fa1967a900360072d00ab4fbff9b5e60746bf3f4fa530001c8e43a505e8bb94")

  val csvFeeder =  csv("./src/test/resources/data/getUser.csv").circular

  def getAUser()={
    repeat(1) {
      feed(csvFeeder)
        .exec(http("Get single user request")
          .get("public/v1/users/${userId}")
          .check(jsonPath("$.data.name").is("${name}"))
          .check(status.in(200,304)))
        .pause(2)
    }
  }

  //scenario
  val scn = scenario("Basic load simulation").exec(getAUser())

  setUp(
    scn.inject(
      nothingFor(5), //pause for a given duration
      atOnceUsers(5), //injects a given number of users at once
      rampUsers(10).during(10.seconds) //injects a given number of users distributed evenly on a time window of a given duration
    ).protocols(httpConf)
  )

}
