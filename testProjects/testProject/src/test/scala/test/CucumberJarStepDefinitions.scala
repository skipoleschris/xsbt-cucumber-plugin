package test

import cucumber.api.scala.{ScalaDsl, EN}
import org.scalatest.matchers.ShouldMatchers
import templemore.sbt.cucumber.RunCucumber
import cucumber.api.DataTable

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
  }

  var tableWorks: Boolean = false

  Given("""^a Data Table:$"""){ (table: DataTable) =>
    tableWorks = true
  }
  Then("""^the Data Table gets parsed correctly$"""){ () =>
    tableWorks should be (true)
  }
}
