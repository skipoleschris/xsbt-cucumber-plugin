package templemore.xsbt.cucumber

import sbt._
import Keys._
import Project.Initialize
import templemore.jruby.JRubyDependencies


// Different output modes for the plugin


// Cucumber settings
case class CucumberSettings(mode: CucumberMode,
                            gemUrl: String,
                            cucumberVersion: String,
                            prawnVersion: String)


object CucumberPlugin extends Plugin with JRubyDependencies {


  val cucumber = TaskKey[Unit]("cucumber")
  val cucumberXSettings = TaskKey[CucumberSettings]("cucumber-settings")

  val cucumberMode = SettingKey[CucumberMode]("cucumber-mode")
  val cucumberGemUrl = SettingKey[String]("cucumber-gem-url")
  val cucumberVersion = SettingKey[String]("cucumber-version")
  val cucumberPrawnVersion = SettingKey[String]("cucumber-prawn-version")



  protected def cucumberTask(settings: CucumberSettings): Unit = {
    println("Cuke settings: " + settings)
  } 

  protected def cucumberSettingsTask: Initialize[Task[CucumberSettings]] = 
    (cucumberMode, cucumberGemUrl, cucumberVersion, cucumberPrawnVersion) map {
      (m, u, cv, pv) => CucumberSettings(m, u, cv, pv)
  }


  private val cucumberConfig = config("cucumber") hide

  val cucumberSettings = Seq(
    ivyConfigurations += cucumberConfig,
    resolvers ++= jRubyResolvers,
    libraryDependencies ++= jRubyDependencies,

    cucumber <<= (cucumberXSettings) map cucumberTask,
    cucumberXSettings <<= cucumberSettingsTask,

    cucumberMode := Normal,
    cucumberGemUrl := "http://rubygems.org/",
    cucumberVersion := "0.10.6",
    cucumberPrawnVersion := "0.8.4"
  )
}
