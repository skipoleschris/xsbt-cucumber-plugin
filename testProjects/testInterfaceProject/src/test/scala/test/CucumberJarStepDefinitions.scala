package test

import cucumber.runtime.{EN, ScalaDsl}
import org.scalatest.matchers.ShouldMatchers
import templemore.sbt.cucumber.RunCucumber

class CucumberJarStepDefinitions extends ScalaDsl with EN with ShouldMatchers {

  private var givenCalled = false
  private var whenCalled = false

  Given("""^an SBT project$""") { () =>
    givenCalled = true
  }

  When("""^the cucumber task is called$""") { () =>
    whenCalled = true
  }

  Then("""^Cucumber is executed against the features and step definitions$""") { () =>
    givenCalled should be (true)
    whenCalled should be (true)
    System.getProperty("testing") should be ("true")
    System.getProperty("demo") should be ("yes")
  }
}

class CucumberSuite extends RunCucumber