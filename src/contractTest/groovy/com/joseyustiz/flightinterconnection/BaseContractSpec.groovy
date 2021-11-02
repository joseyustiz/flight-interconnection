package com.joseyustiz.flightinterconnection

import com.joseyustiz.flightinterconnection.core.GetInterconnectedFlightDelegate
import com.joseyustiz.flightinterconnection.core.GetInterconnectedFlightService
import com.joseyustiz.flightinterconnection.core.port.primary.GetInterconnectedFlightUseCase
import com.joseyustiz.flightinterconnection.infrastructure.config.WebRouterFunctionConfig
import com.joseyustiz.flightinterconnection.infrastructure.primary.web.GetInterconnectedFlightHandler
import com.joseyustiz.flightinterconnection.infrastructure.primary.web.InterconnectedFlightMapper
import com.joseyustiz.flightinterconnection.infrastructure.secondary.*
import io.restassured.module.webtestclient.RestAssuredWebTestClient
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Flux
import spock.lang.Specification

abstract class BaseContractSpec extends Specification {

    void setup() {
        def routeMapper = new RouteMapperImpl();
        def routePort = new RouteHttpAdapter(routeMapper)
        def flightScheduleMapper = new FlightScheduleMapperImpl();
        def schedulePort = new ScheduleHttpAdapter(flightScheduleMapper)
        def calculatePathPort = new CalculatePathAdapter()
        def delegate = new GetInterconnectedFlightDelegate(routePort, schedulePort, calculatePathPort)
        def service = new GetInterconnectedFlightService(delegate)
        service.handle(any() as GetInterconnectedFlightUseCase.Query) >> Flux.empty()
        RestAssuredWebTestClient.webTestClient(WebTestClient.bindToRouterFunction(
                new WebRouterFunctionConfig(new GetInterconnectedFlightHandler(service, InterconnectedFlightMapper.INSTANCE))
                        .interconnectionRoutes()).build())


    }
}
