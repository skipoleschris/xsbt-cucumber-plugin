import sbt._
import Keys._
import templemore.xsbt.cucumber.CucumberPlugin

object BuildSettings {
  val buildOrganization = "templemore"
  val buildScalaVersion = "2.9.1"
  val buildVersion      = "0.4"

  val buildSettings = Defaults.defaultSettings ++
                      Seq (organization := buildOrganization,
                           scalaVersion := buildScalaVersion,
                           version      := buildVersion) ++
                      CucumberPlugin.cucumberSettings
}

object Dependencies {

  val scalaTest = 	"org.scalatest" %% "scalatest" % "1.6.1" % "test"

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
