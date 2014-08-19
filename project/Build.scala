import sbt._
import Keys._

object Settings {
  val buildOrganization = "templemore"
  val buildScalaVersion = "2.11.2"
  val crossBuildScalaVersions = Seq("2.10.4", "2.11.2")
  val buildVersion      = "0.9.0-SNAPSHOT"

  val buildSettings = Defaults.defaultSettings ++
                      Seq (organization  := buildOrganization,
                           scalaVersion  := buildScalaVersion,
                           version       := buildVersion,
                           scalacOptions ++= Seq("-deprecation", "-unchecked", "-encoding", "utf8"),
                           publishTo     := Some(Resolver.file("file",  new File("deploy-repo"))))
}

object Dependencies {

  private val CucumberVersion = "1.2.0-SNAPSHOT"

  def cucumberJvm(scalaVersion: String) =
    "info.cukes" %% "cucumber-scala" % CucumberVersion % "compile"

  val testInterface = "org.scala-tools.testing" % "test-interface" % "0.5" % "compile"
}

object Build extends Build {
  import Dependencies._
  import Settings._

  lazy val parentProject = Project("sbt-cucumber-parent", file ("."),
    settings = buildSettings).aggregate(pluginProject, integrationProject)

  lazy val pluginProject = Project("sbt-cucumber-plugin", file ("plugin"),
    settings = buildSettings ++
               Seq(
                 scalaVersion := "2.10.4",
                 crossScalaVersions := Seq.empty,
                 sbtPlugin := true))

  lazy val integrationProject = Project ("sbt-cucumber-integration", file ("integration"),
    settings = buildSettings ++
               Seq(crossScalaVersions := crossBuildScalaVersions,
               resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
               libraryDependencies <+= scalaVersion { sv => cucumberJvm(sv) },
               libraryDependencies += testInterface))
}

