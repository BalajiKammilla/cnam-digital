Feature: Role Management

  Scenario: Get Role List
    Given I am authenticated as a CNAM user with the permissions: role:list
    When I request the role list
    Then the role list is retrieved