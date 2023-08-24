package test.cases;

import io.gatling.javaapi.core.ChainBuilder;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

/**
 * The class Poke api.
 */
public class PlatziApiSimulation extends Simulation {


    //Http configuration
    private HttpProtocolBuilder httpProtocol = http
            .baseUrl("https://api.escuelajs.co/api/v1")
            .acceptHeader("application/json")
            .contentTypeHeader("application/json");

    //Cosntants
    private static final int USER_COUNT = Integer.parseInt(System.getProperty("USERS", "1"));
    private static final int RAMP_DURATION = Integer.parseInt(System.getProperty("RAMP_DURATION", "10"));
    private static final int RESPONSE_TIME = 800;
    private static final int STATUS_CREATED = 201;
    private static final int STATUS_OK = 200;


    //Metodo que se ejecuta antes de los test
    @Override
    public void before() {
        System.out.println("Simulation Platzi is about to start!");
        System.out.printf("Running test with %d users%n", USER_COUNT);
        System.out.printf("Ramping users over %d seconds%n", RAMP_DURATION);
    }

    //Metodo que se ejecuta al finalizar los test
    @Override
    public void after() {
        System.out.println("Simulation is finished!");
    }

    // HTTP CALLS
    private static ChainBuilder authentication =
            exec(http("Aunthenticate user")
                    .post("/auth/login")
                    .body(ElFileBody("bodies/request/authenticate-credentials.json")).asJson()
                    .check(
                            status().is(STATUS_CREATED),
                            responseTimeInMillis().lte(RESPONSE_TIME),
                            jmesPath("access_token").saveAs("JWT")
                    ));

    private static ChainBuilder decodeJwt =
            exec(http("Decode JWT get info of user")
                    .get("/auth/profile")
                    .header("Authorization", "Bearer #{JWT}")
                    .check(
                            status().is(STATUS_OK),
                            responseTimeInMillis().lte(RESPONSE_TIME),
                            jmesPath("email").is("john@mail.com")
                    ));


    private static ChainBuilder getProductsPagination =
            exec(http("Get Products by Pagination")
                    .get("/products/")
                    .queryParam("offset", "0")
                    .queryParam("limit", "3")
                    .check(
                            status().is(STATUS_OK),
                            responseTimeInMillis().lte(RESPONSE_TIME)
                    ));

    private static ChainBuilder getAllProducts =
            exec(http("Get all products")
                    .get("/products")
                    .check(
                            status().is(STATUS_OK),
                            responseTimeInMillis().lte(RESPONSE_TIME)
                    ));

    private static ChainBuilder getSingleUser =
            exec(http("Get a user")
                    .get("/users/1")
                    .check(
                            status().is(STATUS_OK),
                            responseTimeInMillis().lte(RESPONSE_TIME),
                            jmesPath("id").isEL("1")
                    ));


    private static ChainBuilder checkEmail =
            exec(http("Check if email exists")
                    .post("/users/is-available")
                    .body(ElFileBody("bodies/request/checkEmail.json")).asJson()
                    .check(
                            status().is(STATUS_CREATED),
                            responseTimeInMillis().lte(RESPONSE_TIME),
                            jmesPath("isAvailable").is("false")
                    ));

    private static ChainBuilder uploadFile =
            exec(http("Upload File")
                    .post("/files/upload")
                    .header("Content-Type", "multipart/form-data")
                    .bodyPart(ElFileBodyPart("file", "bodies/request/tiera.jfif").fileName("file"))
                    .check(
                            status().is(STATUS_CREATED),
                            responseTimeInMillis().lte(RESPONSE_TIME),
                            jmesPath("filename").saveAs("filename")
                    ));

    private static ChainBuilder getFileByName =

            exec(http("Get File by Name")
                    .get("/files/#{filename}")
                    .check(
                            status().is(STATUS_OK),
                            responseTimeInMillis().lte(RESPONSE_TIME),
                            header("Content-Type").is("application/octet-stream")
                    ));

    //Configuracion de la prueba, se mandan llamar a cada uno de los servicios
    private ScenarioBuilder scn = scenario("Test of api platzi")
            .exec(authentication)
            .pause(2)
            .exec(decodeJwt)
            .pause(2)
            .exec(getProductsPagination)
            .pause(2)
            .exec(getAllProducts)
            .pause(2)
            .exec(getSingleUser)
            .pause(2)
            .exec(checkEmail)
            .pause(2)
            .exec(uploadFile)
            .pause(2)
            .exec(getFileByName);

    // cargamos la simulacion que se configuro
    {
        setUp(
                scn.injectOpen(
                        nothingFor(5),
                        rampUsers(USER_COUNT).during(RAMP_DURATION)
                )).protocols(httpProtocol);
    }
}

