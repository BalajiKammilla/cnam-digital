Feature: Enrolments

  Background:
    Given I am authenticated as a PUBLIC user

  Scenario: Supporting Documents are Successfully Returned After Submitting Enrolment Details
    When I attach an identity document of type PAS with number 12345
    Then the identity document is successfully attached
    When I submit enrolment details from the following table
      | age  | maritalStatus |
      | 20   | MAR           |
    Then enrolment details are successfully saved
    And the returned list of required supporting document contains APPLICANT_PROOF_OF_MARRIAGE with the document types: MAR
    When I submit enrolment details from the following table
      | age  | maritalStatus | profession |
      | 20   | MAR           |  AT        |
    Then enrolment details are successfully saved
    And the returned list of required supporting document contains APPLICANT_PROOF_OF_PROFESSION with the document types: AT,PEN
    When I submit enrolment details from the following table
      | age  | maritalStatus | paidBy |
      | 25   | MAR           | PAS    |
    Then enrolment details are successfully saved
    And the returned list of required supporting document contains PAYER_PROOF_OF_IDENTITY with the document types: CNI,PAS,CC,NAI