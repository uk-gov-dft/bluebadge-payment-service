@actuator-endpoints
Feature: Verify spring actuator endpoints

  Background:
    * url managementBaseUrl

  Scenario: Verify info endpoint with no auth
    Given path '/actuator/info'
    When method GET
    Then status 200

  Scenario: Verify health endpoint with no auth
    Given path '/actuator/health'
    When method GET
    Then status 200
    And match $.status == 'UP'

  Scenario: Verify loggers endpoint with no auth
    Given path '/actuator/loggers'
    When method GET
    Then status 200