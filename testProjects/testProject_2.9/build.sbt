name := "test-project"

version := "0.8.0"

organization := "templemore"

scalaVersion := "2.9.3"

libraryDependencies ++= Seq(
	"org.scalatest" %% "scalatest" % "1.9.2" % "test"
)

seq(cucumberSettings : _*)

cucumberStepsBasePackage := "test"

cucumberHtmlReport := true

cucumberJunitReport := true 

cucumberJsonReport := true

cucumberSystemProperties := Map("testing" -> "true", "demo" -> "yes")

cucumberJVMOptions := List("-showversion", "-esa")

