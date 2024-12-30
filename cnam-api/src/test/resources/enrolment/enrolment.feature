Feature: Enrolment

  Background:
    Given I am authenticated as a PUBLIC user

  Scenario: Successful Enrolment
    When I attach an identity document of type PAS with number 12345
    Then the identity document is successfully attached
    When I submit enrolment details from the following table
      | age | nationality | maritalStatus | paidBy | mobileNumber   |
      | 20  | CIV         | MAR           | AUT    | +2250187901407 |
    Then enrolment details are successfully saved
    When I attach a supporting document of type MAR as APPLICANT_PROOF_OF_MARRIAGE
    Then the supporting document is successfully attached
    When I attach a supporting document of type AT as APPLICANT_PROOF_OF_PROFESSION
    Then the supporting document is successfully attached
    When I attach a supporting document of type PAS as PAYER_PROOF_OF_IDENTITY
    Then the supporting document is successfully attached
    When I attach my photo
    Then the photo is successfully attached
    When I attach my fingerprints
    Then the fingerprints are successfully attached
    When I attach my signature
    Then the signature is successfully attached
    When I complete the enrolment
    Then the enrolment is successfully completed
    And an alert with the reason ENROLMENT_COMPLETED_NOTIFICATION is sent via the SMS channel to +2250187901407
    And verification tasks are processed
    And an alert with the reason UPDATE_SUBSCRIPTION_PAYER is sent via the SMS channel to +2250187901407
    And an alert with the reason UPDATE_SUBSCRIPTION_PAYER is sent via the NONE channel to +2250187901407
    When I view corrective actions for this enrolment
    Then I find a corrective action of type SUBSCRIPTION_PAYER with status PENDING
    # When I update subscription payer details with CMU number 3843457837159
    # Then subscription payer details are updated
    # And all corrective actions for this enrolment are COMPLETED
    # And verification tasks are processed