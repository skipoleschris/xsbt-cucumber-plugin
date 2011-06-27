package test

import cuke4duke.{EN, ScalaDsl}
import org.scalatest.matchers.ShouldMatchers

class CucumberJarSteps extends ScalaDsl with EN with ShouldMatchers {

  private var givenCalled = false
  private var whenCalled = false

  Given("""^an SBT project$""") {
    givenCalled = true
  }

  When("""^the cucumber task is called$""") {
    whenCalled = true
    // Broken in cuke4duke since cucumber 1.0.0: announce("Hello World")
  }

  Then("""^Cucumber is executed against the features and step definitions$""") {
    givenCalled should be (true)
    whenCalled should be (true)
  }
}
