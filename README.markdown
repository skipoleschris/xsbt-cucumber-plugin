xsbt-cucumber-plugin
====================

An [sbt 0.12.x](https://github.com/harrah/xsbt/wiki) plugin for running [Cucumber](http://cukes.info) features.

IMPORTANT: Release 0.5.0 onwards are a major update that switches from running the ruby version of cucumber (using JRuby) to running cucumber-jvm. This provides a significant improvement in speed and reliability. It also significantly changes the configuration options for the plugin. If you are using a version of this plugin before 0.5.0, please read below and update your project configurations to match the new options.

Provides the ability to run Cucumber-jvm within the SBT environment. Originally based on the [cuke4duke-sbt-plugin](https://github.com/rubbish/cuke4duke-sbt-plugin) by rubbish and my original implementation for SBT 0.7.x. Specifics for this release:

* Works with xsbt 0.12.0
* Works with cucumber-jvm (version 1.0.9 for Scala 2.9.x and version 1.1.1 for Scala 2.10.0-RC1)
* Allows projects compiled and running against Scala 2.9.1, 2.9.2 and 2.10.0-RC1 

## Usage ##
Install the plugin (see later). By default features files go in a 'src/test/features' directory. Step definitions go in 'src/test/scala'. Finally from the sbt console call the task:

    cucumber

The cucumber task can be parameterised with tags or feature names to provide fine grained control of which features get executed. E.g.

    cucumber @demo,~@in-progress

would run features tagged as @demo and not those tagged as @in-progress. Also:

    cucumber "User admin"

would run features with a name matched to "User admin". Multiple arguments can be supplied and honour the following rules:

* arguments starting with @ or ~ will be passed to cucumber using the --tags flag
* arguments starting with anything else will be passed to cucumber using the --name flag

## Writing Features ##
Features are written in text format and are placed in .feature files inside the 'src/test/features' directory. For more info on writing features please see the [Cucumber](http://cukes.info) website.
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
Step definitions can be written in Scala, using the Scala DSL. More information on this api can be obtained from the the [Cucumber](http://cukes.info) website.
For example:

    import cucumber.runtime.{EN, ScalaDsl}
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

NOTE: When running Scala 2.10, change the import to:

    import cucumber.api.scala.{ScalaDsl, EN}

This is required as the Cucumber package structure changed between the 1.0.x and 1.1.x releases

## Project Setup ##
To install the cucumber plugin, add entries to the build plugins file (project/plugins/build.sbt) as follows:

    resolvers += "Templemore Repository" at "http://templemore.co.uk/repo"

    addSbtPlugin("templemore" % "xsbt-cucumber-plugin" % "0.6.2")

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

## Customisation ##
The plugin supports a number of customisations and settings. The following settings can be modified to change the behaviour of the plugin:

### Cucumber Settings ###
* cucumberFeaturesDir - The location of the cucumber features directory within the projects. Defaults to a java.io.File of ./src/test/features
* cucumberStepsBasePackage - The base package from which to search for files containing Steps. Defaults to an empty String (search all packages)
* cucumberExtraOptions - Additional commandline options to pass to the cucumber command. Defaults to an empty List[String]

Note: The cucumberStepsBasePackage should be set in configurations to avoid scanning the entire classpath for files containing Steps

### Output Settings ###
* cucumberPrettyReport - Outputs a pretty printed text file of the executed features instead of writing them to the console. The console display is default to just displaying progress dots. This solves the problem of interleaving of parallel test output when running in a multi-module project. Defaults to the Boolean value false
* cucumberHtmlReport - Outputs an html report of the executed features to a report directory. Defaults to the Boolean value false
* cucumberJunitReport - Outputs a Junit format XML report file of the executed features. Defaults to the Boolean value false
* cucumberJsonReport - Outputs a JSON format report file of the executed features. Defaults to the Boolean value false

If none of the above are set to true then the default output is pretty printed features to the console. It is possible to support multiple outputs in a single run by setting more than one of the above settings to true. For multi-module projects it is recommended to set cucumberPrettyReport to true so that you don't end up with interleaved console output caused by cucumber being run concurrently for each project.

* cucumberPrettyReportFile - The location of the pretty printed text report file. Defaults to a java.io.File of ./target/scala-{scalaVersion}/cucumber.txt
* cucumberHtmlReportDir - The directory for the html report. Defaults to a java.io.File of ./target/scala-{scalaVersion}/cucumber/
* cucumberJunitReportFile - The location of the Junit XML report file. Defaults fo a java.io.File of ./target/scala-{scalaVersion}/cucumber.xml
* cucumberJsonReportFile - The location of the JSON format report file. Defaults fo a java.io.File of ./target/scala-{scalaVersion}/cucumber.json

### JVM Settings ###
* cucumberMaxMemory - The maximum JVM memory to allocate to the JVM process. Defaults to the string "256M"
* cucumberMaxPermGen - The maximum PermGen space for the JVM process. Defaults to the string "64M"
* cucumberSystemProperties - System properties to be passed to the JVM process using the -D flag. Defaults to and empty Map[String, String]
* cucumberJVMOptions - Additional options to be passed to the JVM that runs cucumber. Defaults to an empty List[String]

### Lifecycle Settings ###
* cucumberBefore - A function of type () => Unit that will be run BEFORE cucumber is executed. Defaults to a no-op function
* cucumberAfter - A function of type () => Unit that will be run AFTER cucumber is executed. Defaults to a no-op function

## Roadmap ##

This plugin will continue to track releases of both SBT (0.10 and onwards) and Cucumber-jvm.
Requests for features can be posted to the issues list or emailed to the author.

## Release History ##

### 0.6.2 ###
Upgrade to cucumber-jvm version 1.1.1 to allow compatibility with Scala 2.10.0-RC1 release.

### 0.6.1 ###
Update to allow system properties and other JVM arguments to be passed to the JVM that runs cucumber.
Corrections to documentation.

### 0.6.0 ###
Updated to work the SBT 0.12.0, Scala 2.9.2 and the latest Cucumber-jvm 1.0.14 versions.

### 0.5.0 ###
Moved from ruby implementation of Cucumber to Cucumber-jvm. This changes many of the plugin settings and options. In particular, output options are significantly improved.

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
