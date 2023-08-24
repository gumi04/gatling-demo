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
public class BankApiSimulation extends Simulation {


    //Http configuration
    private HttpProtocolBuilder httpProtocol = http
            .baseUrl("https://api.frankfurter.app")
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
        System.out.println("Simulation  Bakn is about to start!");
        System.out.printf("Running test with %d users%n", USER_COUNT);
        System.out.printf("Ramping users over %d seconds%n", RAMP_DURATION);
    }

    //Metodo que se ejecuta al finalizar los test
    @Override
    public void after() {
        System.out.println("Simulation is finished!");
    }

    // HTTP CALLS
    private static ChainBuilder getRates =
            exec(http("This endpoint returns the latest rates")
                    .get("/latest")
                    .check(
                            status().is(STATUS_OK),
                            responseTimeInMillis().lte(RESPONSE_TIME)
                    ));

    //Configuracion de la prueba, se mandan llamar a cada uno de los servicios
    private ScenarioBuilder scn = scenario("Test of api Bank")
            .exec(getRates);

    // cargamos la simulacion que se configuro
    {
        setUp(
                scn.injectOpen(
                        nothingFor(5),
                        rampUsers(USER_COUNT).during(RAMP_DURATION)
                )).protocols(httpProtocol);
    }
}

