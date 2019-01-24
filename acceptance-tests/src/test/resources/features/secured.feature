@secured
Feature: Verify all end points are secured

  Background:
    * url baseUrl

  Scenario: Denied when say hello without auth header
    Given path 'payment/create'
    And request {}
    When method POST
    Then status 401
