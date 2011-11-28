name := "xsbt-cucumber-plugin"

version := "0.4.1"

organization := "templemore"

scalaVersion := "2.9.1"

sbtPlugin := true

resolvers += "sonatype-snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

libraryDependencies ++= Seq(
    "info.cukes" % "cucumber-core" % "1.0.0-SNAPSHOT" % "test",
	"org.scalatest" %% "scalatest" % "1.6.1" % "test"
)

publishTo := Some(Resolver.file("Local Repo", file((Path.userHome / ".m2" / "repository").toString)))
