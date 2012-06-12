package templemore.xsbt.cucumber

import sbt._

/**
 * @author Chris Turner
 */
trait CucumberDependencies {

  private val cucumberScalaVersion = "1.0.9"

  def cucumberDependencies = Seq("info.cukes" % "cucumber-scala" % cucumberScalaVersion % "test")
}