# Interconnecting Flights

This repository contains a Spring Boot based RESTful API application which serves information about possible direct and interconnected flights (maximum 1 stop) based on the data consumed from external APIs. It makes use of Ryanair public APIs. 

The application returns a list of flights departing from a given departure airport not earlier than the specified departure datetime and arriving to a given arrival airport not later than the specified arrival datetime.
The list should consist of:
* all direct flights if available (for example: DUB - WRO)
* all interconnected flights with a maximum of one stop if available (for example: DUB - STN - WRO)

For interconnected flights the difference between the arrival and the next departure should be 2h or greater

# Technology
The application is mainly based on Spring Projects, with exception of mapstruct, lombok and jgrapht (Graph library), which I used to calculate the all paths of the graph, which where filtered by the business rules: min. connection time.  Additionally, 

## Architecture
* Clean Architecture
* Microservice

# Patterns
* Immutability
* Value Object
* DTO
* Null Object Pattern


## Quality Techniques
* Unit Testing `sh gradlew test` 
* Integration Testing `sh gradlew integrationTest`
* Consumer-Driven Contracts (CDC) `sh gradlew contractTest`, which generates a stub in the build/libs folder with name flight-interconnection-0.0.1-SNAPSHOT-stubs.jar  
* Mutation Testing `sh gradlew pitest`. Its report is at build/reports/pitest/index.html


## Running
In order to run the application you execute the following commands:
* `sh gradlew build`
* `java -jar build/libs/flight-interconnection-0.0.1-SNAPSHOT.jar`.

## Example request and response
Querying flights between two days (departureDateTime=2021-11-02T18:00 and arrivalDateTime=2021-11-03T13:00) from VLC to TFS
* `curl http://localhost:8080/v1/flights/interconnections\?departure=VLC\&arrival=TFS\&departureDateTime=2021-11-02T18:00\&arrivalDateTime=2021-11-03T13:00`
```json
[
  {
    "stops": 2,
    "legs": [
      {
        "departureAirport": "VLC",
        "arrivalAirport": "CRL",
        "departureDateTime": "2021-11-02T20:30",
        "arrivalDateTime": "2021-11-02T22:50"
      },
      {
        "departureAirport": "CRL",
        "arrivalAirport": "TFS",
        "departureDateTime": "2021-11-03T06:35",
        "arrivalDateTime": "2021-11-03T10:05"
      }
    ]
  },
  {
    "stops": 2,
    "legs": [
      {
        "departureAirport": "VLC",
        "arrivalAirport": "BGY",
        "departureDateTime": "2021-11-02T20:10",
        "arrivalDateTime": "2021-11-02T22:05"
      },
      {
        "departureAirport": "BGY",
        "arrivalAirport": "TFS",
        "departureDateTime": "2021-11-03T07:25",
        "arrivalDateTime": "2021-11-03T10:50"
      }
    ]
  }
]
```

**NOTE:** because limitations of Spring Cache annotations to support Reactor and complexity for orchestrating the flows to calculate connections; there is pending work to make the application fully reactive   