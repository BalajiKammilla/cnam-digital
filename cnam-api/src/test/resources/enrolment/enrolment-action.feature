Feature: Enrolment Action List

  Scenario: Get Filtered List Of The Enrolment Action
    Given I am authenticated as a PUBLIC user
    When I attach an identity document of type PAS with number 12345
    Then the identity document is successfully attached
    Given I am authenticated as a CNAM user
    And the IDENTITY_DOCUMENT_ATTACHED action is added to the enrolment
    And the enrolment has 1 actions
