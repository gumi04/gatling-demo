# Gatling API Testing demo 

## Documentation of the apis used 
- [Platzi Api](https://fakeapi.platzi.com/)  
- [Frankfurter Api](https://www.frankfurter.app/docs/)
- [Collections of Api(Insomnia, Postman)](src/test/resources/collections-api)

## Prerequisites

- JDK 11
- Maven
- IDE (Intellij, Vs Code, Eclipse)


## Running Tests

To run all tests, run the following command:

By default it uses:
- USERS = 5
- RAMP_DURATION = 10

```bash
  mvn gatling:test -DUSERS=1 -DRAMP_DURATION=5
```

You can pass 2 parameters for its execution:
- USERS = amount of user to use
- RAMP_DURATION = load of users being tested simultaneously

```bash
  mvn gatling:test -DUSERS=1 -DRAMP_DURATION=5
```
To run a specific simulation

```bash
mvn gatling:test -Dgatling.simulationClass=test.cases.PlatziApiSimulation -DUSERS=1 -DRAMP_DURATION=5
```