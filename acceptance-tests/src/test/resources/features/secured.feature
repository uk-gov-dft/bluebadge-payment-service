@secured
Feature: Verify all end points are secured

  Background:
    * url baseUrl

  Scenario: Create payments endpoint is secure
    Given path 'payments'
    And request {}
    When method POST
    Then status 401
