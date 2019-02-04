@secured
Feature: Verify all end points are secured

  Background:
    * url baseUrl

  Scenario: Create payments endpoint is secure
    Given path 'payments'
    And request {}
    When method POST
    Then status 401

  Scenario: Retrieve payment endpoint is secure
    Given path 'payments/f337f570-a341-4df0-8c66-316a4b6bfa9d'
    When method GET
    Then status 401
