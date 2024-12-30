Feature: CNAM User Management

  Scenario: Create CNAM User
    Given the CNAM user details in the following table:
      | emailAddress       | displayName | password | userRoles     |
      | test01@aptiway.com | Test 01     | Test@123 | USER_MANAGER  |
    And I am authenticated as a CNAM user with the permissions: user:create
    When I create a CNAM user
    Then the CNAM user is created

  Scenario: Retrieve CNAM User List
    Given I am authenticated as a CNAM user with the permissions: user:list
    When I request the CNAM user list
    Then the CNAM user list is retrieved
