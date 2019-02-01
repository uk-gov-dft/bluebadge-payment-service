@secured
Feature: Verify creating payments is secured to the Citizen App

  Background:
    * url baseUrl
    * def result = callonce read('./oauth2.feature')
    * header Authorization = 'Bearer ' + result.accessToken

  Scenario: Payment creation is secured
    Given path 'payments'
    And request {laShortCode:"BIRM", returnUrl:"http://test/return", paymentMessage:"Blue badge test"}
    When method POST
    Then status 403
