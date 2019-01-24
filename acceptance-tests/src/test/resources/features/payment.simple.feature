@payments
Feature: Verify messages create

  Background:
    * url baseUrl
    * def result = callonce read('./oauth2.feature')
    * header Authorization = 'Bearer ' + result.accessToken

  Scenario: Say hello
    Given path 'payment/create'
    And request {}
    When method POST
    Then status 200
    And match $ contains {hello:"world"}