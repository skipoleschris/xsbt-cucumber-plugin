import sbt._
import Keys._

import Versions._

object Settings {
  val buildOrganization = "templemore"
  val buildScalaVersion = scala2_9
  val buildVersion      = "0.7.2"

  val buildSettings = Defaults.defaultSettings ++
                      Seq (organization  := buildOrganization,
                           scalaVersion  := buildScalaVersion,
                           version       := buildVersion,
                           scalacOptions += "-deprecation",
                           publishTo := Some(Resolver.file("file",  new File("deploy-repo"))))
}

object Dependencies {

  def cucumberScala(scalaVersion: String) = {
    def cucumberVersion = if ( scalaVersion.startsWith("2.10") ) CucumberVersionForScala2_10 else CucumberVersionForScala2_9
    "info.cukes" % "cucumber-scala" % cucumberVersion % "compile"      
  }
  val cucumber = "info.cukes" % "cucumber-scala" % "1.0.9" % "compile"

  val testInterface = "org.scala-tools.testing" % "test-interface" % "0.5" % "compile"
}

object Build extends Build {
  import Dependencies._
  import Settings._

  private val crossVersions = Seq(scala2_9, scala2_10)

  lazy val parentProject = Project("sbt-cucumber-parent", file ("."),
    settings = buildSettings ++
               Seq(crossScalaVersions := crossVersions)) aggregate (pluginProject, integrationProject)

  lazy val pluginProject = Project("sbt-cucumber-plugin", file ("plugin"),
    settings = buildSettings ++ 
               Seq(sbtPlugin := true))

  lazy val integrationProject = Project ("sbt-cucumber-integration", file ("integration"),
    settings = buildSettings ++ 
               Seq(crossScalaVersions := crossVersions,
                   libraryDependencies <+= scalaVersion { sv => cucumberScala(sv) },
                   libraryDependencies += testInterface))
}


object Versions {
  val scala2_9 = "2.9.2"
  val scala2_10 = "2.10.0"
  val CucumberVersionForScala2_9 = "1.0.9"
  val CucumberVersionForScala2_10 = "1.1.1"
}