package templemore.xsbt.cucumber

import sbt._
import Keys._
import Project.Initialize
import templemore.jruby.JRubyDependencies

/**
 * @author Chris Turner
 */
object CucumberPlugin extends Plugin with JRubyDependencies {

  val cucumber = InputKey[Unit]("cucumber")
  val cucumberMode = SettingKey[CucumberMode]("cucumber-mode")

  val cucumberJRubyHome = TaskKey[File]("cucumber-jruby-home")
  val cucumberGemDir = TaskKey[File]("cucumber-gem-directory")
  val cucumberMaxMemory = SettingKey[String]("cucumber-jruby-max-memory")
  val cucumberMaxPermGen = SettingKey[String]("cucumber-jruby-max-perm-gen")

  val cucumberVersion = SettingKey[String]("cucumber-version")
  val cucumberPrawnVersion = SettingKey[String]("cucumber-prawn-version")
  val cucumberGemUrl = SettingKey[String]("cucumber-gem-url")
  val cucumberForceGemReload = SettingKey[Boolean]("cucumber-force-gem-reload")

  val cucumberFeaturesDir = SettingKey[File]("cucumber-features-directory")
  val cucumberOptions = SettingKey[Seq[String]]("cucumber-options")

  val cucumberJRubySettings = TaskKey[JRubySettings]("cucumber-jruby-settings")
  val cucumberGemSettings = TaskKey[GemSettings]("cucumber-gem-settings")
  val cucumberTestSettings = TaskKey[CucumberSettings]("cucumber-settings")

  protected def cucumberTask(argTask: TaskKey[Seq[String]]) =
    (argTask, cucumberJRubySettings, cucumberGemSettings, cucumberTestSettings) map {
      (args: Seq[String], jRubySettings, gemSettings, cukeSettings) => {
        println("Cucumber task")
        println("JRuby Settings: " + jRubySettings)
        println("Gem Settings: " + gemSettings)
        println("Cuke Settings: " + cukeSettings)
        println("Argumens: ")
        args foreach println
      }
    }

  protected def jRubySettingsTask: Initialize[Task[JRubySettings]] =
    (cucumberJRubyHome, cucumberGemDir, cucumberMaxMemory, cucumberMaxPermGen) map {
      (home, gems, mem, permGen) => {
        //TODO: Need to use the JRuby classpath
        //TODO: Need to pass a logger instance
        JRubySettings(home, gems, List[String](), mem, permGen, StdoutOutput)
      }
    }

  protected def gemSettingsTask: Initialize[Task[GemSettings]] =
    (cucumberVersion, cucumberPrawnVersion, cucumberGemUrl, cucumberForceGemReload) map {
      (cv, pv, gs, fr) => GemSettings(cv, pv, gs, fr)
    }

  protected def cucumberSettingsTask: Initialize[Task[CucumberSettings]] =
    (cucumberFeaturesDir, cucumberOptions, cucumberMode) map {
      (fd, o, m) => {
        //TODO: Need to use the required path
        CucumberSettings(fd, List[String](), optionsForMode(m) ++ o)
      }
    }

  private def optionsForMode(mode: CucumberMode) = mode match {
    case Developer => List[String]()
    case HtmlReport => List[String]()
    case PdfReport => List[String]()
    case _ => List[String]()
  }

  private def jRubyBaseDir = new File(System.getProperty("user.home"), ".jruby")

  private val cucumberConfig = config("cucumber") hide

  val cucumberSettings = Seq(
    ivyConfigurations += cucumberConfig,
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
    cucumberPrawnVersion := "0.8.4",
    cucumberGemUrl := "http://rubygems.org/",
    cucumberForceGemReload := false,

    cucumberFeaturesDir <<= (baseDirectory) { _ / "features" },
    cucumberOptions := List[String]()
  )
}