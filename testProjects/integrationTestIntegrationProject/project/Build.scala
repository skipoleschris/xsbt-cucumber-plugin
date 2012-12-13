import sbt._
import Keys._
import templemore.sbt.cucumber.CucumberPlugin

object BuildSettings {
  val buildOrganization = "templemore"
  val buildScalaVersion = "2.9.2"
  val buildVersion      = "0.7.2"

  val buildSettings = Defaults.defaultSettings ++
                      Seq (organization := buildOrganization,
                           scalaVersion := buildScalaVersion,
                           version      := buildVersion)

  val cucumberSettings = CucumberPlugin.cucumberSettingsWithIntegrationTestPhaseIntegration ++
                         Seq(CucumberPlugin.cucumberHtmlReport := true,
                             CucumberPlugin.cucumberPrettyReport := true)
}

object Dependencies {

  val scalaTest = "org.scalatest" %% "scalatest" % "1.7.2" % "it"
}

object TestProjectBuild extends Build {
  import Dependencies._
  import BuildSettings._

  lazy val integrationTestProject = Project ("integration-test-project", file ("."),
           settings = buildSettings ++ cucumberSettings ++ Seq (libraryDependencies += scalaTest))
           .configs(IntegrationTest)
           .settings(Defaults.itSettings : _*)
}
