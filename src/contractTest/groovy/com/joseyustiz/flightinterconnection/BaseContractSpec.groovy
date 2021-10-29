package com.joseyustiz.flightinterconnection


import com.joseyustiz.flightinterconnection.core.service.GetInterconnectedFlightService
import com.joseyustiz.flightinterconnection.infrastructure.config.WebRouterFunctionConfig
import com.joseyustiz.flightinterconnection.infrastructure.primary.web.GetInterconnectedFlightHandler
import com.joseyustiz.flightinterconnection.infrastructure.primary.web.InterconnectedFlightMapper
import io.restassured.module.webtestclient.RestAssuredWebTestClient
import org.springframework.test.web.reactive.server.WebTestClient
import spock.lang.Specification

abstract class BaseContractSpec extends Specification {

    void setup() {
        RestAssuredWebTestClient.webTestClient(WebTestClient.bindToRouterFunction(
                new WebRouterFunctionConfig(new GetInterconnectedFlightHandler(new GetInterconnectedFlightService(), InterconnectedFlightMapper.INSTANCE)).interconnectionRoutes()).build())


    }
}
