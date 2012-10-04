name := "test-project"

version := "0.6.0"

organization := "templemore"

scalaVersion := "2.10.0-M6"

libraryDependencies ++= Seq(
	"org.scalatest" % "scalatest_2.10.0-M6" % "1.9-2.10.0-M6-B2" % "test"
)

seq(cucumberSettings : _*)

cucumberStepsBasePackage := "test"

cucumberHtmlReport := true

cucumberJunitReport := true 

cucumberJsonReport := true
