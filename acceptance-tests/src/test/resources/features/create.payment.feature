@payments
Feature: Verify creating payments

  Background:
    * url baseUrl
    * def result = callonce read('./oauth2-citizen-app.feature')
    * header Authorization = 'Bearer ' + result.accessToken

  Scenario: Payment creation
    Given path 'payments'
    And request {laShortCode:"BIRM", returnUrl:"http://test/return", paymentMessage:"Blue badge test"}
    When method POST
    And print response
    Then status 200
    And match $.data contains {paymentJourneyUuid:"#notnull", nextUrl:"#notnull"}

  Scenario: Payment creation - LA does not have payments enabled. Results in common response error.
    Given path 'payments'
    And request {laShortCode:"ANGL", returnUrl:"http://", paymentMessage:"Blue badge test"}
    When method POST
    Then status 503
    And print response
    And match $ contains {error:"#notnull"}
    And match $.error.message contains "No GOV Pay profile found for LA: ANGL"

  Scenario: GOV Pay api key invalid. Results in common response error.
    Given path 'payments'
    And request {laShortCode:"BLACK", returnUrl:"http://", paymentMessage:"Blue badge test"}
    When method POST
    Then status 503
    And print response
    And match $ contains {error:"#notnull"}
    And match $.error.message contains "GOV Pay api key not accepted"

  Scenario: Unknown LA then Bad Request common response error.
    Given path 'payments'
    And request {laShortCode:"TEST", returnUrl:"http://", paymentMessage:"Blue badge test"}
    When method POST
    And print response
    Then status 400
    And match $ contains {error:"#notnull"}
    And match $.error.errors[0].message contains "Invalid LA short code"
    And match $.error.errors[0].reason contains "Local authority not found for: TEST"
    And match $.error.errors[0].field contains "laShortCode"

  Scenario: Missing LA Short Code
    Given path 'payments'
    And request {returnUrl:"http://", paymentMessage:"Blue badge test"}
    When method POST
    Then status 400
    And print response
    And match $ contains {error:"#notnull"}
    And match $.error.errors[0].message contains "NotBlank.newPaymentDetails.laShortCode"

  Scenario: Missing return URL
    Given path 'payments'
    And request {laShortCode:"testing", paymentMessage:"Blue badge test"}
    When method POST
    Then status 400
    And print response
    And match $ contains {error:"#notnull"}
    And match $.error.errors[0].message contains "NotBlank.newPaymentDetails.returnUrl"

  Scenario: Missing payment message
    Given path 'payments'
    And request {laShortCode:"testing", returnUrl:"http://"}
    When method POST
    Then status 400
    And print response
    And match $ contains {error:"#notnull"}
    And match $.error.errors[0].message contains "NotBlank.newPaymentDetails.paymentMessage"
