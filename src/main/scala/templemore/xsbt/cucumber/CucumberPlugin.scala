package templemore.xsbt.cucumber

import sbt._
import Keys._
import Project.Initialize

/**
 * @author Chris Turner
 */
object CucumberPlugin extends Plugin with CucumberIntegration {

  private val CucumberVersionForScala2_9 = "1.0.9"
  private val CucumberVersionForScala2_10 = "1.0.10"

  type LifecycleCallback = () => Unit

  val cucumber = InputKey[Int]("cucumber")
  val cucumberTestSettings = TaskKey[CucumberSettings]("cucumber-settings")
  val cucumberOptions = TaskKey[CucumberOptions]("cucumber-options")

  val cucumberMaxMemory = SettingKey[String]("cucumber-max-memory")
  val cucumberMaxPermGen = SettingKey[String]("cucumber-max-perm-gen")

  val cucumberFeaturesDir = SettingKey[File]("cucumber-features-directory")
  val cucumberStepsBasePackage = SettingKey[String]("cucumber-steps-base-package")
  val cucumberExtraOptions = SettingKey[Seq[String]]("cucumber-extra-options")

  val cucumberHtmlReportDir = SettingKey[Option[File]]("cucumber-html-report")
  val cucumberJsonReportFile = SettingKey[Option[File]]("cucumber-json-report")
  val cucumberJunitReportFile = SettingKey[Option[File]]("cucumber-junit-report")

  val cucumberBefore = SettingKey[LifecycleCallback]("cucumber-before")
  val cucumberAfter = SettingKey[LifecycleCallback]("cucumber-after")

  protected def cucumberTask(argTask: TaskKey[Seq[String]]) =
    (argTask, cucumberTestSettings, cucumberOptions, streams) map(testWithCucumber)

  protected def cucumberSettingsTask: Initialize[Task[CucumberSettings]] =
    (cucumberMaxMemory, cucumberMaxPermGen, fullClasspath in Test, streams) map {
      (mm, mpg, cp, s) => {
        CucumberSettings(mm, mpg, cp.toList.map(_.data), LoggedOutput(s.log))
      }
    }

  protected def cucumberOptionsTask: Initialize[Task[CucumberOptions]] =
    (cucumberFeaturesDir, cucumberStepsBasePackage, cucumberExtraOptions,
     cucumberHtmlReportDir, cucumberJsonReportFile, cucumberJunitReportFile,
     cucumberBefore, cucumberAfter) map {
      (fd, bp, o, htmlRD, jsonRF, junitRF, bf, af) => {
        CucumberOptions(fd, bp, optionsForReporting(htmlRD, jsonRF, junitRF) ++ o, bf, af)
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

    cucumberMaxMemory := "256M",
    cucumberMaxPermGen := "64M",

    cucumberFeaturesDir <<= (baseDirectory) { _ / "src" / "test" / "features" },
    cucumberStepsBasePackage := "",
    cucumberExtraOptions := List[String](),

    cucumberHtmlReportDir := None,
    cucumberJsonReportFile := None,
    cucumberJunitReportFile := None,

    cucumberBefore := defaultBefore,
    cucumberAfter := defaultAfter
  )
}