name := "test-project2_10"

version := "0.7.3-SNAPSHOT"

organization := "templemore"

scalaVersion := "2.10.2"

libraryDependencies ++= Seq(
	"org.scalatest" %% "scalatest" % "1.9.1" % "test"
)

seq(cucumberSettingsWithTestPhaseIntegration : _*)

cucumberStepsBasePackage := "test"

cucumberHtmlReport := true

cucumberJunitReport := true 

cucumberJsonReport := true
