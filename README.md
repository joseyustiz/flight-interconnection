# Interconnecting Flights

This repository contains a Spring Boot based RESTful API application which serves information about possible direct and interconnected flights (maximum 1 stop) based on the data consumed from external APIs. It makes use of Ryanair public APIs. 

The application should return a list of flights departing from a given departure airport not earlier than the specified departure datetime and arriving to a given arrival airport not later than the specified arrival datetime.
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
* Unit Testing `gradle test` 
* Integration Testing `gradle integrationTest`
* Consumer-Driven Contracts (CDC) `gradle contractTest`, which generates a stub in the build/libs folder with name flight-interconnection-0.0.1-SNAPSHOT-stubs.jar  
* Mutation Testing `gradle pitest`. Its report is at build/reports/pitest/index.html


## Running
In order to run the application you execute the following commands:
* `gradle build`
* `java -jar build/libs/flight-interconnection-0.0.1-SNAPSHOT.jar`.
## Healthcheck
Let’s health-check our brand new service:
* `curl http://localhost:8100/actuator/health`