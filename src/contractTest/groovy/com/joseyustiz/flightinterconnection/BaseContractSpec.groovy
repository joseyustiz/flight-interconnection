package com.joseyustiz.flightinterconnection

import com.joseyustiz.flightinterconnection.core.GetInterconnectedFlightService
import com.joseyustiz.flightinterconnection.core.port.primary.GetInterconnectedFlightUseCase
import com.joseyustiz.flightinterconnection.infrastructure.config.WebRouterFunctionConfig
import com.joseyustiz.flightinterconnection.infrastructure.primary.web.GetInterconnectedFlightHandler
import com.joseyustiz.flightinterconnection.infrastructure.primary.web.InterconnectedFlightMapper
import com.joseyustiz.flightinterconnection.infrastructure.secondary.CalculatePathAdapter
import com.joseyustiz.flightinterconnection.infrastructure.secondary.RouteHttpAdapter
import com.joseyustiz.flightinterconnection.infrastructure.secondary.ScheduleHttpAdapter
import io.restassured.module.webtestclient.RestAssuredWebTestClient
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Flux
import spock.lang.Specification

abstract class BaseContractSpec extends Specification {

    void setup() {
        def routePort = new RouteHttpAdapter()
        def schedulePort = new ScheduleHttpAdapter()
        def calculatePathPort = new CalculatePathAdapter()
        def service = new GetInterconnectedFlightService(routePort, schedulePort, calculatePathPort)
        service.handle(any() as GetInterconnectedFlightUseCase.Query) >> Flux.empty()
        RestAssuredWebTestClient.webTestClient(WebTestClient.bindToRouterFunction(
                new WebRouterFunctionConfig(new GetInterconnectedFlightHandler(service, InterconnectedFlightMapper.INSTANCE))
                        .interconnectionRoutes()).build())


    }
}
