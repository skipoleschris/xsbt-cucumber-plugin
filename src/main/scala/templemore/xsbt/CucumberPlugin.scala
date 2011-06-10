package templemore.xsbt.cucumber

import sbt._
import Keys._
import Project.Initialize


// Different output modes for the plugin
sealed trait CucumberMode
object Normal extends CucumberMode { override def toString = "Normal Console Output" }
object Developer extends CucumberMode { override def toString = "Developer Console Ouptut" }
object HtmlReport extends CucumberMode { override def toString = "Html Report Output" }
object PdfReport extends CucumberMode { override def toString = "Pdf Report Output" }


// Cucumber settings
case class CucumberSettings(mode: CucumberMode,
                            gemUrl: String,
                            cucumberVersion: String,
                            prawnVersion: String)


object CucumberPlugin extends Plugin {

  private val jrubyVersion = "1.6.1"
  private val cuke4DukeVersion = "0.4.4"
  private val picoContainerVersion = "2.11.2"


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
  private val cukesMavenRepo = "Cuke4Duke Maven Repository" at "http://cukes.info/maven"

  val cucumberSettings = Seq(
    ivyConfigurations += cucumberConfig,
    resolvers += cukesMavenRepo,
    libraryDependencies ++= Seq(
      "org.jruby" % "jruby-complete" % jrubyVersion % "cuke->default",
      "cuke4duke" % "cuke4duke" % cuke4DukeVersion % "cuke->default",
      "org.picocontainer" % "picocontainer" % picoContainerVersion % "cuke->default"
    ),

    cucumber <<= (cucumberXSettings) map cucumberTask,
    cucumberXSettings <<= cucumberSettingsTask,

    cucumberMode := Normal,
    cucumberGemUrl := "http://rubygems.org/",
    cucumberVersion := "0.10.6",
    cucumberPrawnVersion := "0.8.4"
  )
}
