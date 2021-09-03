package simulations

import io.gatling.core.scenario.Simulation
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef.jdbcFeeder

import java.util.UUID
import scala.concurrent.duration._


class RampUsersLoadSimulation extends Simulation {

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

  //request in DB
  val news = jdbcFeeder("jdbc:postgresql://localhost:6432/news", "news", "", "SELECT ID, TITLE FROM NEWS LIMIT 1000")
  val newsItem = feed(news).exec(http("NewsItem")
    .get("/news/${id}")
    .check(status.is(200)))

  val updateNews = feed(news)
    .exec(session => session.set("description", UUID.randomUUID().toString))
    .exec(http("Update News")
      .put("/news/${id}")
      //.body(StringBody("""{"title"}: "${title}", "description": "${description}"}""")).asJson
      .body(ElFileBody("news.json")).asJson //"${title}"
      .check(status.is(204))
      .check(jsonPath("$.id").saveAs("id"))
    ).doIf(session => session("id").asOption[String].isDefined) {
    exec(
      http("Delete")
        .delete("/news/${id}")
    )
  }



  val scn = scenario("Ramp Users load simulation").exec(getAUser())

  setUp(
    scn.inject(
      nothingFor(5),
      constantUsersPerSec(10).during(10.seconds), //каждую секунду 10 пользователей в течении 10 сек, т е 10*10=100
      //injects users at a constant rate, defined in users per second, during a given duration.
      // Users will be injected at regular intervals.
      rampUsersPerSec(1).to(5).during(20.seconds) //от 1 до 5 пользователей в секунду
      //injects users from starting rate to target rate, defined in users per second, during a given duration.
      // Users will be injected at regular intervals.
    ).protocols(httpConf)
  )

}
