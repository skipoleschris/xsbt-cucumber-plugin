package templemore.sbt.cucumber

import sbt._
import Keys._
import Project.Initialize
import templemore.sbt.util._

/**
 * @author Chris Turner
 */
object CucumberPlugin extends Plugin with Integration {

  private val projectVersion = "0.7.2"

  type LifecycleCallback = () => Unit

  val cucumber = InputKey[Int]("cucumber")
  val cucumberTestSettings = TaskKey[JvmSettings]("cucumber-settings")
  val cucumberOptions = TaskKey[Options]("cucumber-options")
  val cucumberOutput = TaskKey[Output]("cucumber-output")

  val cucumberMaxMemory = SettingKey[String]("cucumber-max-memory")
  val cucumberMaxPermGen = SettingKey[String]("cucumber-max-perm-gen")
  val cucumberSystemProperties = SettingKey[Map[String, String]]("cucumber-system-properties")
  val cucumberJVMOptions = SettingKey[List[String]]("cucumber-jvm-options")

  val cucumberMainClass = SettingKey[String]("cucumber-main-class")
  val cucumberFeaturesLocation = SettingKey[String]("cucumber-features-location")
  val cucumberStepsBasePackage = SettingKey[String]("cucumber-steps-base-package")
  val cucumberExtraOptions = SettingKey[List[String]]("cucumber-extra-options")

  val cucumberPrettyReport = SettingKey[Boolean]("cucumber-pretty-report")
  val cucumberHtmlReport = SettingKey[Boolean]("cucumber-html-report")
  val cucumberJunitReport = SettingKey[Boolean]("cucumber-junit-report")
  val cucumberJsonReport = SettingKey[Boolean]("cucumber-json-report")

  val cucumberPrettyReportFile = SettingKey[File]("cucumber-pretty-report-file")
  val cucumberHtmlReportDir = SettingKey[File]("cucumber-html-report-dir")
  val cucumberJsonReportFile = SettingKey[File]("cucumber-json-report-file")
  val cucumberJunitReportFile = SettingKey[File]("cucumber-junit-report-file")

  val cucumberBefore = SettingKey[LifecycleCallback]("cucumber-before")
  val cucumberAfter = SettingKey[LifecycleCallback]("cucumber-after")

  protected def cucumberTask(argTask: TaskKey[Seq[String]]) =
    (argTask, cucumberTestSettings, cucumberOptions, cucumberOutput, streams) map {
      (args, settings, opt, out, s) => cuke(args, settings, opt, out, s) match {
        case 0 => 0
        case _ => sys.error("There were failed tests.")
      }
    }

  protected def cucumberSettingsTask: Initialize[Task[JvmSettings]] =
    (fullClasspath in Test, cucumberMainClass, streams, cucumberSystemProperties, cucumberJVMOptions, cucumberMaxMemory, cucumberMaxPermGen) map {
      (cp, mc, s, sp, jvmopt, mm, mpg) => JvmSettings(cp.toList.map(_.data), mc, LoggedOutput(s.log), sp, jvmopt, Some(mm), Some(mpg))
    }

  protected def cucumberOptionsTask: Initialize[Task[Options]] =
    (cucumberFeaturesLocation, cucumberStepsBasePackage, cucumberExtraOptions,
     cucumberBefore, cucumberAfter) map ((fl, bp, o, bf, af) => Options(fl, bp, o, bf, af))

  protected def cucumberOutputTask: Initialize[Task[Output]] =
    (cucumberPrettyReport, cucumberHtmlReport, cucumberJunitReport, cucumberJsonReport,
     cucumberPrettyReportFile, cucumberHtmlReportDir, cucumberJunitReportFile, cucumberJsonReportFile) map {
      (pR, hR, juR, jsR, pRF, hRD, juRF, jsRF) => {
        Output(pR, hR, juR, jsR, pRF, hRD, juRF, jsRF)
      }
    }

  private def defaultBefore() = {}
  private def defaultAfter() = {}

  private def cucumberMain(scalaVersion: String) = 
    if ( scalaVersion.startsWith("2.10") ) "cucumber.api.cli.Main" else "cucumber.cli.Main"

  val cucumberSettings: Seq[Setting[_]] = Seq(
    resolvers += "Templemore Repository" at "http://templemore.co.uk/repo",
    libraryDependencies += "templemore" %% "sbt-cucumber-integration" % projectVersion % "test",

    cucumber <<= inputTask(cucumberTask),
    cucumberTestSettings <<= cucumberSettingsTask,
    cucumberOptions <<= cucumberOptionsTask,
    cucumberOutput <<= cucumberOutputTask,

    cucumberMaxMemory := "256M",
    cucumberMaxPermGen := "64M",
    cucumberSystemProperties := Map.empty[String, String],
    cucumberJVMOptions := Nil,

    cucumberMainClass <<= (scalaVersion) { sv => cucumberMain(sv) },
    cucumberFeaturesLocation := "classpath:",
    cucumberStepsBasePackage := "",
    cucumberExtraOptions := List.empty[String],

    cucumberPrettyReport := false,
    cucumberHtmlReport := false,
    cucumberJunitReport := false,
    cucumberJsonReport := false,

    cucumberPrettyReportFile <<= (scalaVersion, target) { (sv, t) => t / "scala-%s".format(sv) / "cucumber.txt" },
    cucumberHtmlReportDir <<= (scalaVersion, target) { (sv, t) => t / "scala-%s".format(sv) / "cucumber-report" },
    cucumberJsonReportFile <<= (scalaVersion, target) { (sv, t) => t / "scala-%s".format(sv) / "cucumber.json" },
    cucumberJunitReportFile <<= (scalaVersion, target) { (sv, t) => t / "scala-%s".format(sv) / "cucumber.xml" },

    cucumberBefore := defaultBefore,
    cucumberAfter := defaultAfter
  )

  val cucumberSettingsWithTestPhaseIntegration = cucumberSettings ++ Seq(
    testFrameworks += new TestFramework("templemore.sbt.cucumber.CucumberFramework")
  ) 

  val cucumberSettingsWithIntegrationTestPhaseIntegration = cucumberSettings ++ Seq(
    testFrameworks in IntegrationTest += new TestFramework("templemore.sbt.cucumber.CucumberFramework"),
    libraryDependencies += "templemore" %% "sbt-cucumber-integration" % projectVersion % "it"
  ) 
}
