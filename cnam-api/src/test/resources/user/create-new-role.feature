Feature: New Role Creation

  Scenario: Create A New Role
    Given I am authenticated as a CNAM user with the permissions: role:create
    When I request to create the new role
    Then the new role is created