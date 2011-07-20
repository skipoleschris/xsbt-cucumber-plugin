name := "test-project"

version := "0.2"

organization := "templemore"

scalaVersion := "2.9.0-1"

libraryDependencies ++= Seq(
	"org.scalatest" % "scalatest_2.9.0" % "1.4.1" % "test"
)

seq(cucumberSettings : _*)
