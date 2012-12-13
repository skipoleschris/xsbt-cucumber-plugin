name := "test-project2_10"

version := "0.7.2"

organization := "templemore"

scalaVersion := "2.10.0-RC2"

libraryDependencies ++= Seq(
	"org.scalatest" % "scalatest_2.10.0-RC2" % "1.8" % "test"
)

seq(cucumberSettingsWithTestPhaseIntegration : _*)

cucumberStepsBasePackage := "test"

cucumberHtmlReport := true

cucumberJunitReport := true 

cucumberJsonReport := true
