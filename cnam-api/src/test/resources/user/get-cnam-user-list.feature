Feature: CNAM User Management

  Scenario: Get CNAM User List
    Given I am authenticated as a CNAM user with the permissions: user:list
    When I request the CNAM user list
    Then the CNAM user list is retrieved
