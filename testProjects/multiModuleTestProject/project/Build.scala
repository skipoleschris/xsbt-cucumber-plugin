import sbt._
import Keys._
import templemore.sbt.cucumber.CucumberPlugin

object BuildSettings {
  val buildOrganization = "templemore"
  val buildScalaVersion = "2.9.2"
  val buildVersion      = "0.7.0"

  val buildSettings = Defaults.defaultSettings ++
                      Seq (organization := buildOrganization,
                           scalaVersion := buildScalaVersion,
                           version      := buildVersion)

  // NOTE: If not worried about integration with the 'test' task then use:
  //   CucumberPlugin.cucumberSettings instead of CucumberPlugin.cucumberSettingsWithTestPhaseIntegration
  val cucumberSettings = CucumberPlugin.cucumberSettingsWithTestPhaseIntegration ++
                         Seq(CucumberPlugin.cucumberHtmlReport := true,
                             CucumberPlugin.cucumberPrettyReport := true)
}

object Dependencies {

  val scalaTest = "org.scalatest" %% "scalatest" % "1.7.2" % "test"

  // NOTE: This dependency is only required when using 'test' task integration
  val testIntegration = "templemore" %% "sbt-cucumber-integration" % "0.7.0" % "test"
 
  val testDeps = Seq(scalaTest, testIntegration)
}

object TestProjectBuild extends Build {
  import Dependencies._
  import BuildSettings._

  lazy val multiModuleTestProject = Project ("test-project", file ("."),
           settings = buildSettings) aggregate (jarProject, warProject)


  lazy val jarProject = Project ("jar-project", file ("jar-project"),
           settings = buildSettings ++ cucumberSettings ++ Seq (libraryDependencies ++= testDeps))

  lazy val warProject = Project ("war-project", file ("war-project"),
           settings = buildSettings ++ cucumberSettings ++ Seq (libraryDependencies ++= testDeps)) dependsOn (jarProject)
}
