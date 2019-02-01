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

  Scenario: Create and then retrieve
    Given path 'payments/' + paymentJourneyUuid
    When method GET
    And print response
    Then status 200
    And match $.data contains {paymentJourneyUuid:"#notnull", status:"#notnull", reference:"#notnull"}

