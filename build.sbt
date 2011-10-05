name := "xsbt-cucumber-plugin"

version := "0.4"

organization := "templemore"

scalaVersion := "2.9.1"

sbtPlugin := true

libraryDependencies ++= Seq(
    "org.jruby" % "jruby-complete" % "1.6.4" % "test",
	"org.scalatest" %% "scalatest" % "1.6.1" % "test"
)

publishTo := Some(Resolver.file("Local Repo", file((Path.userHome / ".m2" / "repository").toString)))
