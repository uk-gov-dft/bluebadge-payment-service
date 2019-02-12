@payments
Feature: Verify retrieving payments

  Background:
    * url baseUrl
    * def result = callonce read('./oauth2-citizen-app.feature')
    * header Authorization = 'Bearer ' + result.accessToken
    * def dbConfig = { username: 'developer',  ***REMOVED*** }
    * def DbUtils = Java.type('uk.gov.service.bluebadge.test.utils.DbUtils')
    * def db = new DbUtils(dbConfig)
    * def setup = db.runScript('acceptance-test-data.sql')
    * def createResult = callonce read('./create.payment.success.feature')
    * def paymentJourneyUuid = createResult.paymentJourneyUuid

  Scenario: Retrieve newly created payment
    Given path 'payments/' + paymentJourneyUuid
    When method GET
    And print response
    Then status 200
    And match $.data contains {paymentJourneyUuid:"#notnull", status:"created", reference:"#notnull"}

  Scenario: Retrieve unknown UUID
    Given path 'payments/f337f570-a341-4df0-8c66-316a4b6bfa9d'
    When method GET
    And print response
    Then status 404
    And match $ contains {error:"#notnull"}
    And match $.error contains {message:"NotFound.Payment"}

  Scenario: Retrieve invalid UUID
    Given path 'payments/something'
    When method GET
    And print response
    Then status 400
    And match $ contains {error:"#notnull"}
    And match $.error.errors[0] contains {message:"Invalid payment journey UUID"}

