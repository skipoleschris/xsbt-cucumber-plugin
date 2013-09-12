import sbt._
import Keys._

object Settings {
  val buildOrganization = "templemore"
  val buildScalaVersion = "2.9.2"
  val buildVersion      = "0.7.3-SNAPSHOT"

  val buildSettings = Defaults.defaultSettings ++
                      Seq (organization  := buildOrganization,
                           scalaVersion  := buildScalaVersion,
                           version       := buildVersion,
                           scalacOptions += "-deprecation",
                           publishTo     := Some(Resolver.file("file",  new File("deploy-repo"))))
}

object Dependencies {

  private val CucumberVersionForScala2_9 = "1.0.9"
  private val CucumberVersionForScala2_10 = "1.1.4"

  def cucumberScala(scalaVersion: String) = {
    if ( scalaVersion.startsWith("2.10") ) {
      "info.cukes" %% "cucumber-scala" % CucumberVersionForScala2_10 % "compile"
    } else {
      "info.cukes" % "cucumber-scala" % CucumberVersionForScala2_9 % "compile"
    }
  }

  val testInterface = "org.scala-tools.testing" % "test-interface" % "0.5" % "compile"
}

object Build extends Build {
  import Dependencies._
  import Settings._

  lazy val parentProject = Project("sbt-cucumber-parent", file ("."),
    settings = buildSettings ++
               Seq(crossScalaVersions := Seq("2.9.2", "2.10.2"))) aggregate (pluginProject, integrationProject)

  lazy val pluginProject = Project("sbt-cucumber-plugin", file ("plugin"),
    settings = buildSettings ++
               Seq(sbtPlugin := true))

  lazy val integrationProject = Project ("sbt-cucumber-integration", file ("integration"),
    settings = buildSettings ++ 
               Seq(crossScalaVersions := Seq("2.9.2", "2.10.2"),
                   libraryDependencies <+= scalaVersion { sv => cucumberScala(sv) },
                   libraryDependencies += testInterface))
}

