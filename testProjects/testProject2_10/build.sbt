name := "test-project"

version := "0.6.2"

organization := "templemore"

scalaVersion := "2.10.0-RC1"

libraryDependencies ++= Seq(
	"org.scalatest" % "scalatest_2.10.0-RC1" % "1.8-2.10.0-RC1-B1" % "test"
)

seq(cucumberSettings : _*)

cucumberStepsBasePackage := "test"

cucumberHtmlReport := true

cucumberJunitReport := true 

cucumberJsonReport := true
