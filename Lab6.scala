
import scala.concurrent.duration._
import io.gatling.core.Predef._
import io.gatling.http.Predef._

object RandomGenerator {
  def randomString(length: Int): String = {
    val SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890"
    val salt = new StringBuilder
    val rnd = new scala.util.Random
    while (salt.length < length) { // length of the random string.
      val index = (rnd.nextFloat() * SALTCHARS.length).asInstanceOf[Int]
      salt.append(SALTCHARS.charAt(index))
    }
    val saltStr = salt.toString
    saltStr
  }
  def randomAuthorRequest(): String =
    """{"id" : 32,"firstName":"""".stripMargin + RandomGenerator.randomString(25) +"""",
                                                                                     |"lastName":"""".stripMargin + RandomGenerator.randomString(25) +""""}""".stripMargin
}
class AuthorSimulation extends Simulation {

  val httpProtocol = http
    .baseUrl("https://fakerestapi.azurewebsites.net/")


  val post = scenario("Post Author")
    .exec(sessionPost => {
      val sessionPostUpdate = sessionPost.set("postrequest", RandomGenerator.randomAuthorRequest())
      sessionPostUpdate
    })
    .exec(
      http("Post Author")
        .post("/api/v1/Authors/")
        .body(StringBody("${postrequest}")).asJson
    )

  val get = scenario("Get Author")
    .exec(sessionPost => {
      val sessionPostUpdate = sessionPost.set("postrequest", RandomGenerator.randomAuthorRequest())
      sessionPostUpdate
    })
    .exec(
      http("Post Author")
        .post("/api/v1/Authors/")
        .body(StringBody("${postrequest}")).asJson
        .check(jsonPath("$.id").saveAs("AuthorId"))
    )
    .exitHereIfFailed
    .exec(
      http("Get Author")
        .get("/api/v1/Authors/${AuthorId}")
    )

  val put = scenario("Put Author")
    .exec(sessionPost => {
      val sessionPostUpdate = sessionPost.set("postrequest", RandomGenerator.randomAuthorRequest())
      sessionPostUpdate
    })
    .exec(
      http("Post Author")
        .post("/api/v1/Authors/")
        .body(StringBody("${postrequest}")).asJson
        .check(jsonPath("$.id").saveAs("AuthorId"))
    )
    .exitHereIfFailed
    .exec(sessionPut => {
      val sessionPutUpdate = sessionPut.set("putrequest", RandomGenerator.randomAuthorRequest())
      sessionPutUpdate
    })
    .exec(
      http("Put Author")
        .put("/api/v1/Authors/${AuthorId}")
        .body(StringBody("${putrequest}")).asJson
    )

  val delete = scenario("Delete Author")
    .exec(sessionPost => {
      val sessionPostUpdate = sessionPost.set("postrequest", RandomGenerator.randomAuthorRequest())
      sessionPostUpdate
    })
    .exec(
      http("Post Author")
        .post("/api/v1/Authors")
        .body(StringBody("${postrequest}")).asJson
        .check(jsonPath("$.id").saveAs("AuthorId"))
    )
    .exitHereIfFailed
    .exec(sessionPut => {
      val sessionPutUpdate = sessionPut.set("putrequest", RandomGenerator.randomAuthorRequest())
      sessionPutUpdate
    })
    .exec(
      http("Delete Author")
        .delete("/api/v1/Authors/${AuthorId}")
    )

  setUp(post.inject(rampUsers(30).during(5.seconds)).protocols(httpProtocol),
    get.inject(rampUsers(40).during(5.seconds)).protocols(httpProtocol),
    put.inject(rampUsers(30).during(5.seconds)).protocols(httpProtocol),
    delete.inject(rampUsers(30).during(5.seconds)).protocols(httpProtocol))

}