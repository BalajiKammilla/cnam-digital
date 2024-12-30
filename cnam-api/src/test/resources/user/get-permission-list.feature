Feature: Permission Management

  Scenario: Get Permission List
    Given I am authenticated as a CNAM user with the permissions: permission:list
    When I request the permissions list
    Then the permissions list is retrieved
