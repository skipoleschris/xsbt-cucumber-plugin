import sbt._
import Keys._
import templemore.xsbt.cucumber.CucumberPlugin

object BuildSettings {
  val buildOrganization = "templemore"
  val buildScalaVersion = "2.9.2"
  val buildVersion      = "0.6.2"

  val buildSettings = Defaults.defaultSettings ++
                      Seq (organization := buildOrganization,
                           scalaVersion := buildScalaVersion,
                           version      := buildVersion) ++
                      CucumberPlugin.cucumberSettings ++
                      Seq (CucumberPlugin.cucumberHtmlReport := true,
                           CucumberPlugin.cucumberPrettyReport := true)
}

object Dependencies {

  val scalaTest = 	"org.scalatest" %% "scalatest" % "1.7.2" % "test"

  val testDeps = Seq(scalaTest)
}

object TestProjectBuild extends Build {
  import Dependencies._
  import BuildSettings._

  lazy val multiModuleTestProject = Project ("test-project", file ("."),
           settings = buildSettings) aggregate (jarProject, warProject)


  lazy val jarProject = Project ("jar-project", file ("jar-project"),
           settings = buildSettings ++ Seq (libraryDependencies ++= testDeps))

  lazy val warProject = Project ("war-project", file ("war-project"),
           settings = buildSettings ++ Seq (libraryDependencies ++= testDeps)) dependsOn (jarProject)
}
