package com.joseyustiz.flightinterconnection

import com.joseyustiz.flightinterconnection.infrastructure.primary.web.GetRyanairInterconnectedFlightController
import io.restassured.module.webtestclient.RestAssuredWebTestClient


import spock.lang.Specification;

abstract class BaseMockMvcSpec extends Specification {

    def setup() {
        RestAssuredWebTestClient.standaloneSetup(new GetRyanairInterconnectedFlightController())
    }
}
