Feature:FingerprintsImage

  Scenario: Successfully Retrieved The Fingerprints Image
    Given I am authenticated as a PUBLIC user
    When I attach an identity document of type PAS with number 12345
    Then the identity document is successfully attached
    When I attach my fingerprints
    Then the fingerprints are successfully attached
    Given I am authenticated as a CNAM user
    And I fetch the fingerprint images
    And retrieved the fingerprint images successfully
