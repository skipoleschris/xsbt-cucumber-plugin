xsbt-cucumber-plugin
====================

An [sbt 0.11.x](https://github.com/harrah/xsbt/wiki) plugin for running [Cucumber](http://cukes.info) features under [cuke4duke](http://github.com/aslakhellesoy/cuke4duke).

Provides the ability to run Cucumber via Cuke4Duke within the SBT environment. Originally based on the [cuke4duke-sbt-plugin](https://github.com/rubbish/cuke4duke-sbt-plugin) by rubbish and my original implementation for SBT 0.7.x. Specifics for this release:

* Works with xsbt 0.11.0, 0.11.1 and 0.11.2
* Works with Cucumber 1.0.0
* Works with cuke4duke 0.4.4
* Allows projects compiled and running against Scala 2.9.1

## Usage ##
Install the plugin (see later). Be default features files go in a 'features' directory at the root of the project. Step definitions go in "src/test/scala'. Finally from the sbt console call the task:

    cucumber

The cucumber task can be parameterised with tags or feature names to provide fine grained control of which features get executed. E.g.

    cucumber @demo,~@in-progress

would run features tagged as @demo and not those tagged as @in-progress. Also:

    cucumber "User admin"

would run features with a name matched to "User admin". Multiple arguments can be supplied and honour the following rules:

* arguments starting with @ or ~ will be passed to cucumber using the --tags flag
* arguments starting with anything else will be passed to cucumber using the --name flag

## Writing Features ##
Features are written in text format and are placed in .feature files inside the 'features' directory. For more info on writing features please see the [Cucumber](http://cukes.info) website.
For example:

    Feature: Cucumber
      In order to implement BDD in my Scala project
      As a developer
      I want to be able to run Cucumber from with SBT

      Scenario: Execute feature with console output
        Given A SBT project
        When I run the cucumber goal
        Then Cucumber is executed against my features and step definitions

The location of the features can be changed by overriding a plugin setting (see below).

## Writing Step Defitions ##
Step definitions can be written in Scala, using the cuke4duke Scala DSL. More information on this api can be obtained from the the [cuke4duke wiki page for scala](http://wiki.github.com/aslakhellesoy/cuke4duke/scala).
For example:

    import cuke4duke.{EN, ScalaDsl}
    import org.scalatest.matchers.ShouldMatchers

    class CucumberSteps extends ScalaDsl with EN with ShouldMatchers {

      private var givenCalled = false
      private var whenCalled = false

      Given("""^A SBT project$""") {
        givenCalled = true
      }

      When("""^I run the cucumber goal$""") {
        whenCalled = true
      }

      Then("""^Cucumber is executed against my features and step definitions$""") {
        givenCalled should be (true)
        whenCalled should be (true)
      }
    }

## Project Setup ##
To install the cucumber plugin, add entries to the build plugins file (project/plugins/build.sbt) as follows:

    resolvers += "Templemore Repository" at "http://templemore.co.uk/repo"

    addSbtPlugin("templemore" % "xsbt-cucumber-plugin" % "0.4.1")

### Basic Configuration ###
To add the cucumber plugin settings to a basic project, just add the following to the build.sbt file:

    seq(cucumberSettings : _*)

The testProjects/testProject in the plugin source repository shows this configuration.

### Full Configuration ###
To add the cucumber plugin settings to a full configuration (often a multi-module) project, the best way is to implement a project/Build.scala file:

    import sbt._
    import Keys._
    import templemore.xsbt.cucumber.CucumberPlugin

    object TestProjectBuild extends Build {
        import Dependencies._
        import BuildSettings._

        lazy val myProject = Project ("my-project", file ("."),
                                      settings = Defaults.defaultSettings ++ CucumberPlugin.cucumberSettings)
    }

The testProjects/multiModuleTestProject in the plugin source repository shows this setup in a multi-module project.

## Gems ##
All gems are automatically installed the first time that the cucumber plugin is run. These gems are installed to the default location {user.home}/.jruby/gems so that they are cached for all projects using the cucumber plugin. The location of the cache directory can be overridden in the settings.

A task is provided to delete the cache directory contents:

    cucumber-clean-gems

## Customisation ##
The plugin supports a number of customisations and settings. The following settings can be modified to change the behaviour of the plugin:

### Mode ###
* cucumberMode = The mode to run cucumber in. Defaults to the value templemore.xsbt.cucumber.Normal

The four supported modes are:

* templemore.xsbt.cucumber.Normal - Runs cucumber and outputs results to the console
* templemore.xsbt.cucumber.Developer - Runs cucumber and outputs results, snippets and source to the console
* templemore.xsbt.cucumber.HtmlReport - Runs cucumber and outputs an html report of the results
* templemore.xsbt.cucumber.PdfReport - Runs cucumber and outputs a pdf report of the results

### General Settings ###
* cucumberJRubyHome - The location for the JRuby home. Defaults to a java.io.File of {user.home}/.jruby
* cucumberGemDir - The location of the Gem cache directory. Defaults to a java.io.File of {cucumberJRubyHome}/gems
* cucumberMaxMemory - The maximum JVM memory to allocate to the JRuby process. Defaults to the string "256M"
* cucumberMaxPermGen - The maximum PermGen space for the JRuby process. Defaults to the string "64M"

### Gem Settings ###
* cucumberVersion - The version of Cucumber to use. Defaults to the string "1.0.0"
* cucumberCuke4DukeVersion - The version of Cuke4Duke to use. Defaults to the string "0.4.4"
* cucumberPrawnVersion - The version of the Prawn PDF generator to use. Defaults to the string "0.8.4"
* cucumberGemUrl - The URL for downloading Gems. Default to the string "http://rubygems.org/"
* cucumberForceGemReload - Whether to force reloading of all gems, even if they exist in the cache. Defaults to false

### Cucumber Settings ###
* cucumberFeaturesDir - The location of the cucumber features directory within the projects. Defaults to a java.io.File of ./features
* cucumberOptions - Custom options to pass to the cucumber command. Defaults to an empty List[String]

### Output Settings ###
* cucumberHtmlReportFile - The location of the html report file. Defaults fo a java.io.File of ./target/cucumber-report/cucumber.html
* cucumberPdfReportFile - The location of the pdf report file. Defaults fo a java.io.File of ./target/cucumber-report/cucumber.pdf

### Lifecycle Settings ###
* cucumberBefore - A function of type () => Unit that will be run BEFORE cucumber is executed. Defaults to a no-op function
* cucumberAfter - A function of type () => Unit that will be run AFTER cucumber is executed. Defaults to a no-op function

## Roadmap ##

This plugin will continue to track releases of both SBT (0.10 and onwards) and Cucumber.
Requests for features can be posted to the issues list or emailed to the author.

Current plan is to upgrade to the next major version of SBT and then to switch to cucumber-jvm as soon as there is a stable release.

## Release History ##


### 0.4.1 ###
Updated to build versions for xsbt 0.11.0, 0.11.1 and 0.11.2

### 0.4 ###
Updated to support xsbt 0.11.0 & scala 2.9.1

### 0.3 ###
Bug fix to correct --names parameter which should in fact be --name. Thanks to https://github.com/zvozin for the patch.

### 0.2 ###
Updated to be compatible with SBT 0.10.1 release. This release contains no code changes, it is just a recompilation with the build properties updated.

### 0.1 ###
Initial release. Complete rewrite of the plugin to support SBT 0.10.
