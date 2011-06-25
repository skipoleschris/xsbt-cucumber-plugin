package templemore.xsbt.cucumber

import sbt._
import Keys._
import Project.Initialize
import templemore.jruby.{Cucumber, GemInstaller, JRubyDependencies}

/**
 * @author Chris Turner
 */
object CucumberPlugin extends Plugin with CucumberIntegration with JRubyDependencies {

  type LifecycleCallback = () => Unit

  val cucumber = InputKey[Int]("cucumber")
  val cucumberMode = SettingKey[CucumberMode]("cucumber-mode")

  val cucumberJRubyHome = TaskKey[File]("cucumber-jruby-home")
  val cucumberGemDir = TaskKey[File]("cucumber-gem-directory")
  val cucumberMaxMemory = SettingKey[String]("cucumber-jruby-max-memory")
  val cucumberMaxPermGen = SettingKey[String]("cucumber-jruby-max-perm-gen")

  val cucumberVersion = SettingKey[String]("cucumber-version")
  val cucumberCuke4DukeVersion = SettingKey[String]("cucumber-cuke4duke-version")
  val cucumberPrawnVersion = SettingKey[String]("cucumber-prawn-version")
  val cucumberGemUrl = SettingKey[String]("cucumber-gem-url")
  val cucumberForceGemReload = SettingKey[Boolean]("cucumber-force-gem-reload")

  val cucumberFeaturesDir = SettingKey[File]("cucumber-features-directory")
  val cucumberOptions = SettingKey[Seq[String]]("cucumber-options")

  val cucumberHtmlReportFile = SettingKey[File]("cucumber-html-report")
  val cucumberPdfReportFile = SettingKey[File]("cucumber-pdf-report")

  val cucumberJRubySettings = TaskKey[JRubySettings]("cucumber-jruby-settings")
  val cucumberGemSettings = TaskKey[GemSettings]("cucumber-gem-settings")
  val cucumberTestSettings = TaskKey[CucumberSettings]("cucumber-settings")

  val cucumberBefore = SettingKey[LifecycleCallback]("cucumber-before")
  val cucumberAfter = SettingKey[LifecycleCallback]("cucumber-after")

  val cucumberCleanGems = TaskKey[Unit]("cucumber-clean-gems")

  protected def cucumberTask(argTask: TaskKey[Seq[String]]) =
    (argTask, cucumberJRubySettings, cucumberGemSettings, cucumberTestSettings, streams) map(testWithCucumber)

  protected def jRubySettingsTask: Initialize[Task[JRubySettings]] =
    (cucumberJRubyHome, cucumberGemDir, fullClasspath in Test,
     cucumberMaxMemory, cucumberMaxPermGen, streams) map {
      (home, gems, cp, mem, permGen, s) => {
        JRubySettings(home, gems, cp.toList.map(_.data), mem, permGen, LoggedOutput(s.log))
      }
    }

  protected def gemSettingsTask: Initialize[Task[GemSettings]] =
    (cucumberVersion, cucumberCuke4DukeVersion, cucumberPrawnVersion, cucumberGemUrl, cucumberForceGemReload) map {
      (cv, cdv, pv, gs, fr) => GemSettings(cv, cdv, pv, gs, fr)
    }

  protected def cucumberSettingsTask: Initialize[Task[CucumberSettings]] =
    (cucumberFeaturesDir, classDirectory in Test, cucumberOptions,
     cucumberMode, cucumberHtmlReportFile, cucumberPdfReportFile,
     cucumberBefore, cucumberAfter) map {
      (fd, cd, o, m, htmlRF, pdfRF, bf, af) => {
        CucumberSettings(fd, cd, optionsForMode(m, htmlRF, pdfRF) ++ o, bf, af)
      }
    }

  protected def cleanGemsTask: Initialize[Task[Unit]] = (cucumberJRubySettings) map(cleanGemCache)

  private def defaultBefore() = {}
  private def defaultAfter() = {}

  val cucumberSettings = Seq(
    resolvers ++= jRubyResolvers,
    libraryDependencies ++= jRubyDependencies,

    cucumber <<= inputTask(cucumberTask),

    cucumberJRubySettings <<= jRubySettingsTask,
    cucumberGemSettings <<= gemSettingsTask,
    cucumberTestSettings <<= cucumberSettingsTask,

    cucumberMode := Normal,

    cucumberJRubyHome := jRubyBaseDir,
    cucumberGemDir := jRubyBaseDir / "gems",
    cucumberMaxMemory := "256M",
    cucumberMaxPermGen := "64M",

    cucumberVersion := "1.0.0",
    cucumberCuke4DukeVersion := "0.4.4",
    cucumberPrawnVersion := "0.8.4",
    cucumberGemUrl := "http://rubygems.org/",
    cucumberForceGemReload := false,

    cucumberFeaturesDir <<= (baseDirectory) { _ / "features" },
    cucumberHtmlReportFile <<= (target) { _ / "cucumber-report" / "cucumber.html" },
    cucumberPdfReportFile <<= (target) { _ / "cucumber-report" / "cucumber.pdf" },
    cucumberOptions := List[String](),

    cucumberBefore := defaultBefore,
    cucumberAfter := defaultAfter,

    cucumberCleanGems <<= cleanGemsTask
  )
}