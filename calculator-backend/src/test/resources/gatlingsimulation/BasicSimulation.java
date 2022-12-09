package gatling.quarkus;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class BasicSimulation extends Simulation {

  public HttpProtocolBuilder httpProtocol = http
    .baseUrl("http://localhost:8090")
    .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
    .doNotTrackHeader("1")
    .acceptLanguageHeader("en-US,en;q=0.5")
    .acceptEncodingHeader("gzip, deflate")
    .userAgentHeader("Mozilla/5.0 (Windows NT 5.1; rv:31.0) Gecko/20100101 Firefox/31.0");

  public ScenarioBuilder scn = scenario("BasicSimulation")
    .exec(http("request_1")
      .get("/api/myresource"))
    .pause(5);

  {
    setUp(
      scn.injectOpen(atOnceUsers(1))
    ).protocols(httpProtocol);
  }
}
