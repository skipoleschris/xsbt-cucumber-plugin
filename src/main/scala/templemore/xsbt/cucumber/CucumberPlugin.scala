package templemore.xsbt.cucumber

import sbt._
import Keys._
import Project.Initialize

/**
 * @author Chris Turner
 */
object CucumberPlugin extends Plugin with CucumberIntegration {

  private val CucumberVersionForScala2_9 = "1.0.9"
  private val CucumberVersionForScala2_10 = "1.0.14"

  type LifecycleCallback = () => Unit

  val cucumber = InputKey[Int]("cucumber")
  val cucumberTestSettings = TaskKey[CucumberSettings]("cucumber-settings")
  val cucumberOptions = TaskKey[CucumberOptions]("cucumber-options")
  val cucumberOutput = TaskKey[CucumberOutput]("cucumber-output")

  val cucumberMaxMemory = SettingKey[String]("cucumber-max-memory")
  val cucumberMaxPermGen = SettingKey[String]("cucumber-max-perm-gen")

  val cucumberFeaturesDir = SettingKey[File]("cucumber-features-directory")
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
    (argTask, cucumberTestSettings, cucumberOptions, cucumberOutput, streams) map(testWithCucumber)

  protected def cucumberSettingsTask: Initialize[Task[CucumberSettings]] =
    (cucumberMaxMemory, cucumberMaxPermGen, fullClasspath in Test, streams) map {
      (mm, mpg, cp, s) => {
        CucumberSettings(mm, mpg, cp.toList.map(_.data), LoggedOutput(s.log))
      }
    }

  protected def cucumberOptionsTask: Initialize[Task[CucumberOptions]] =
    (cucumberFeaturesDir, cucumberStepsBasePackage, cucumberExtraOptions,
     cucumberBefore, cucumberAfter) map {
      (fd, bp, o, bf, af) => {
        CucumberOptions(fd, bp, o, bf, af)
      }
    }

  protected def cucumberOutputTask: Initialize[Task[CucumberOutput]] =
    (cucumberPrettyReport, cucumberHtmlReport, cucumberJunitReport, cucumberJsonReport,
     cucumberPrettyReportFile, cucumberHtmlReportDir, cucumberJunitReportFile, cucumberJsonReportFile) map {
      (pR, hR, juR, jsR, pRF, hRD, juRF, jsRF) => {
        CucumberOutput(pR, hR, juR, jsR, pRF, hRD, juRF, jsRF)
      }
    }

  private def defaultBefore() = {}
  private def defaultAfter() = {}

  private def cucumberVersion(scalaVersion: String) = 
    if ( scalaVersion.startsWith("2.10") ) CucumberVersionForScala2_10 else CucumberVersionForScala2_9

  val cucumberSettings = Seq(
    libraryDependencies <+= scalaVersion { sv =>
      "info.cukes" % "cucumber-scala" % cucumberVersion(sv) % "test"      
    },

    cucumber <<= inputTask(cucumberTask),
    cucumberTestSettings <<= cucumberSettingsTask,
    cucumberOptions <<= cucumberOptionsTask,
    cucumberOutput <<= cucumberOutputTask,

    cucumberMaxMemory := "256M",
    cucumberMaxPermGen := "64M",

    cucumberFeaturesDir <<= (baseDirectory) { _ / "src" / "test" / "features" },
    cucumberStepsBasePackage := "",
    cucumberExtraOptions := List[String](),

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
}