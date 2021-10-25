package contracts.flightinterconnection

import org.springframework.cloud.contract.spec.Contract

Contract.make{
    request {
        method 'GET'
        url '/v1/flights/interconnections?departure=DUB&arrival=WRO&departureDateTime=2018-03-01T07:00&arrivalDateTime=2018-03-03T21:00'

        headers {
            contentType('application/json')
        }
    }
    response {
        status OK()
        headers {
            contentType(applicationJson())
        }
    }
}
