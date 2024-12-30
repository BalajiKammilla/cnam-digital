Feature: CNAM User Authentication

  Scenario: Create Access Token for CNAM User
    When I request an access token using username "test@aptiway.com" and password "Password#01"
    Then an access token is generated

  Scenario Outline: Create Access Token for CNAM User Fails
    When I request an access token using username "<username>" and password "<password>"
    Then the request fails with status "<status>", code "<code>", and message "<message>"

    Examples:
      | username         | password | status | code        | message          |
      | test@gmail.com   | Test@123 | 404    | E404-51-002 | user not found   |
      | test@aptiway.com | Test@123 | 401    | E401-51-008 | invalid password |
