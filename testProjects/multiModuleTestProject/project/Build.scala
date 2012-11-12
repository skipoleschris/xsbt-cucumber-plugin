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
}

object TestProjectBuild extends Build {
  import Dependencies._
  import BuildSettings._

  lazy val multiModuleTestProject = Project ("test-project", file ("."),
           settings = buildSettings) aggregate (jarProject, warProject)


  lazy val jarProject = Project ("jar-project", file ("jar-project"),
           settings = buildSettings ++ cucumberSettings ++ Seq (libraryDependencies += scalaTest))

  lazy val warProject = Project ("war-project", file ("war-project"),
           settings = buildSettings ++ cucumberSettings ++ Seq (libraryDependencies += scalaTest)) dependsOn (jarProject)
}
