package templemore.xsbt.cucumber

import sbt._
import Keys._
import Project.Initialize

/**
 * @author Chris Turner
 */
object CucumberPlugin extends Plugin with CucumberIntegration with CucumberDependencies {

  type LifecycleCallback = () => Unit

  val cucumber = InputKey[Int]("cucumber")
  val cucumberTestSettings = TaskKey[CucumberSettings]("cucumber-settings")
  val cucumberOptions = TaskKey[CucumberOptions]("cucumber-options")

  val cucumberVersion = SettingKey[String]("cucumber-version")
  val cucumberMaxMemory = SettingKey[String]("cucumber-max-memory")
  val cucumberMaxPermGen = SettingKey[String]("cucumber-max-perm-gen")

  val cucumberFeaturesDir = SettingKey[File]("cucumber-features-directory")
  val cucumberExtraOptions = SettingKey[Seq[String]]("cucumber-extra-options")
  val cucumberMode = SettingKey[CucumberMode]("cucumber-mode")
  val cucumberHtmlReportFile = SettingKey[File]("cucumber-html-report")
  val cucumberBefore = SettingKey[LifecycleCallback]("cucumber-before")
  val cucumberAfter = SettingKey[LifecycleCallback]("cucumber-after")

  protected def cucumberTask(argTask: TaskKey[Seq[String]]) =
    (argTask, cucumberTestSettings, cucumberOptions, streams) map(testWithCucumber)

  protected def cucumberSettingsTask: Initialize[Task[CucumberSettings]] =
    (cucumberVersion, cucumberMaxMemory, cucumberMaxPermGen, fullClasspath in Test, streams) map {
      (v, mm, mpg, cp, s) => {
        CucumberSettings(v, mm, mpg, cp.toList.map(_.data), LoggedOutput(s.log))
      }
    }

  protected def cucumberOptionsTask: Initialize[Task[CucumberOptions]] =
    (cucumberFeaturesDir, classDirectory in Test, cucumberExtraOptions,
     cucumberMode, cucumberHtmlReportFile, cucumberBefore, cucumberAfter) map {
      (fd, cd, o, m, htmlRF, bf, af) => {
        CucumberOptions(fd, cd, optionsForMode(m, htmlRF) ++ o, bf, af)
      }
    }

  private def defaultBefore() = {}
  private def defaultAfter() = {}

  val cucumberSettings = Seq(
    libraryDependencies ++= cucumberDependencies,

    cucumber <<= inputTask(cucumberTask),
    cucumberTestSettings <<= cucumberSettingsTask,
    cucumberOptions <<= cucumberOptionsTask,

    cucumberMode := Normal,

    cucumberMaxMemory := "256M",
    cucumberMaxPermGen := "64M",
    cucumberVersion := "1.0.9",

    cucumberFeaturesDir <<= (baseDirectory) { _ / "features" },
    cucumberHtmlReportFile <<= (target) { _ / "cucumber-report" / "cucumber.html" },
    cucumberExtraOptions := List[String](),

    cucumberBefore := defaultBefore,
    cucumberAfter := defaultAfter
  )
}