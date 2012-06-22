@example-war
Feature: Cucumber in War Project
  In order to implement BDD in my Scala project
  As a developer
  I want to be able to run Cucumber from with SBT

  Scenario: Execute feature in a War Project
    Given an SBT project
    When the cucumber task is called
    Then Cucumber is executed against the features and step definitions
