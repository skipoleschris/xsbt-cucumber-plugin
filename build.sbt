name := "xsbt-cucumber-plugin"

version := "0.3"

organization := "templemore"

scalaVersion := "2.8.1"

sbtPlugin := true

libraryDependencies ++= Seq(
	"org.scalatest" %% "scalatest" % "1.5" % "test"
)

publishTo := Some(Resolver.file("Local Repo", file((Path.userHome / ".m2" / "repository").toString)))
