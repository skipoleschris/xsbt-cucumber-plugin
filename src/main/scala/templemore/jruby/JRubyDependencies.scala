package templemore.jruby

import sbt._

/**
 * @author Chris Turner
 */
trait JRubyDependencies {

  private val jrubyVersion = "1.6.4"
  private val cuke4DukeVersion = "0.4.4"
  private val picoContainerVersion = "2.11.2"

  def jRubyDependencies = Seq("org.jruby" % "jruby-complete" % jrubyVersion % "test",
                              "cuke4duke" % "cuke4duke" % cuke4DukeVersion % "test",
                              "org.picocontainer" % "picocontainer" % picoContainerVersion % "test")

  private val cukesMavenRepo = "Cuke4Duke Maven Repository" at "http://cukes.info/maven"

  def jRubyResolvers = Seq(cukesMavenRepo)
  def jRubyBaseDir = new File(System.getProperty("user.home"), ".jruby")
}