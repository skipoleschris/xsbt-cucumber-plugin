name := "test-project"

version := "0.4"

organization := "templemore"

scalaVersion := "2.9.1"

libraryDependencies ++= Seq(
	"org.scalatest" %% "scalatest" % "1.6.1" % "test"
)

seq(cucumberSettings : _*)
