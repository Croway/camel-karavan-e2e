Feature: HomePage

  Background:
    Given Browser is opened on "http://camel-karavan:8080"
    Given wait page load

  Scenario: The title can be seen
    Then the page title "Karavan" can be seen

  Scenario: Test kamelets page
    Given click on xpath "//*[@id=\"kamelets\"]"
    Given write "test" in xpath "//*[@id=\"title\"]"