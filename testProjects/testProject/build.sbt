name := "test-project"

version := "0.5.0"

organization := "templemore"

scalaVersion := "2.9.2"

libraryDependencies ++= Seq(
	"org.scalatest" %% "scalatest" % "1.7.2" % "test"
)

seq(cucumberSettings : _*)

cucumberStepsBasePackage := "test"

cucumberHtmlReportDir <<= (target) { d => Some(d / "cucumber-report") }

cucumberJsonReportFile <<= (target) { f => Some(f / "cucumber.json") }

cucumberJunitReportFile <<= (target) { f => Some(f / "cucumber-junit.xml") }
