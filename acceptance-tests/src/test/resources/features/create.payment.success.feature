@payments
Feature: Verify creating payments

  Background:
    * url baseUrl
    * def result = callonce read('./oauth2-citizen-app.feature')
    * header Authorization = 'Bearer ' + result.accessToken
    * def dbConfig = { username: 'developer',  ***REMOVED*** }
    * def DbUtils = Java.type('uk.gov.service.bluebadge.test.utils.DbUtils')
    * def db = new DbUtils(dbConfig)
    * def setup = db.runScript('acceptance-test-data.sql')

  Scenario: Payment creation
    Given path 'payments'
    And request {laShortCode:"BIRM", returnUrl:"http://test/return", paymentMessage:"Blue badge test"}
    When method POST
    And print response
    Then status 200
    And match $.data contains {paymentJourneyUuid:"#notnull", nextUrl:"#notnull"}
    * def paymentJourneyUuid = $.data.paymentJourneyUuid
