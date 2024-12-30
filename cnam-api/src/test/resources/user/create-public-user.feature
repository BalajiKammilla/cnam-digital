Feature: Public User Registration

  Scenario: Create Public User
    When I submit a request to create a public user
    Then the user is created and an OTP challenge is generated
    When I submit the correct OTP to a generated challenge
    Then the OTP is verified with the kind REGISTRATION_OTP_VERIFIED
    When I request an access token
    Then an access token is generated
