package contracts.flightinterconnection

import org.springframework.cloud.contract.spec.Contract

Contract.make{
    request {
        method 'GET'
        url '/v1/flights/interconnections'
        headers {
            contentType('application/json')
        }
    }
    response {
        status OK()
        body(text: "Hello")
        headers {
            contentType(applicationJson())
        }
    }
}
