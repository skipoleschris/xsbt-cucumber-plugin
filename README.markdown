xsbt-cucumber-plugin
====================

An [sbt 0.12.x](https://github.com/harrah/xsbt/wiki) plugin for running [Cucumber](http://cukes.info) features.

### IMPORTANT NOTES ABOUT THIS RELEASE (0.7.x) ###
It adds the ability to run cucumber as a standalone SBT task but also as a test runner within the standard 'test' task (either within the Test or within the IntegrationTest configs). To facilitate this, there has been one significant change that you should be aware of: the default location for feature files has changed from the src/test/features directory to the classpath. This is required as running as a test framework only has access to the test classpath. Features should therefore now live under src/test/resources. It is possible to change this back to another location by overriding the cucumberFeaturesLocation setting, but if you change this to anything other than the classpath then the 'test' task will not be able to find features.

## Overview ##
Provides the ability to run Cucumber-jvm within the SBT environment. Originally based on the [cuke4duke-sbt-plugin](https://github.com/rubbish/cuke4duke-sbt-plugin) by rubbish and my original implementation for SBT 0.7.x. Specifics for this release:

* Works with xsbt 0.12.0
* Works with cucumber-jvm (version 1.0.9 for Scala 2.9.x and version 1.1.1 for Scala 2.10.0-RC1/RC2)
* Allows projects compiled and running against Scala 2.9.1, 2.9.2 and 2.10.0-RC1/RC2 

## Usage - Standalone Task ##
Install the plugin (see later). By default features files go in the 'src/test/resources' directory. Step definitions go in 'src/test/scala'. Finally from the sbt console call the task:

    cucumber

The cucumber task can be parameterised with tags or feature names to provide fine grained control of which features get executed. E.g.

    cucumber @demo,~@in-progress

would run features tagged as @demo and not those tagged as @in-progress. Also:

    cucumber "User admin"

would run features with a name matched to "User admin". Multiple arguments can be supplied and honour the following rules:

* arguments starting with @ or ~ will be passed to cucumber using the --tags flag
* arguments starting with anything else will be passed to cucumber using the --name flag

## Usage - Test Framework ##
Install the plugin and additional test framework integration (see later). Feature files MUST go in the 'src/test/resources' directory as only the classpath is visible to test frameworks. Step definitions go in 'src/test/scala'. 

There must also be present somewhere in the test code the following class:

  class CucumberSuite extends templemore.sbt.cucumber.RunCucumber

This is required to trigger cucumber to run (as SBT only runs tests that extends a specific base class or have a specific annotation). There MUST only be one instance of a class extending RunCucumber in the test code as we only want cucumber to be executed once! Finally from the sbt console call the task:

    test

Note that none of the configuration options apply when running via a test framework. This is because the SBT test integration does not allow any access to these settings. Cucumber will be executed with pretty output to the console, searching the classpath from its root for features and executing all tests found in packages.

It is also possible to filter exactly which features get executed by using the test-only task. To do this, specify the CucumberSuite that you defined above as the test to run and then use either the tag or name approach already described as the test arguments:

      test-only mypackage.CucumberSuite -- @demo
      test-only mypackage.CucumberSuite -- "User admin"

## Usage - Integration Test Framework ##
Install the plugin and additional integration test framework integration (see later). Feature files MUST go in the 'src/it/resources' directory as only the classpath is visible to test frameworks. Step definitions go in 'src/it/scala'. 

There must also be present somewhere in the test code the following class:

  class CucumberSuite extends templemore.sbt.cucumber.RunCucumber

This is required to trigger cucumber to run (as SBT only runs tests that extends a specific base class or have a specific annotation). There MUST only be one instance of a class extending RunCucumber in the test code as we only want cucumber to be executed once! Finally from the sbt console call the task:

    it:test

Note that none of the configuration options apply when running via a test framework. This is because the SBT integration test integration does not allow any access to these settings. Cucumber will be executed with pretty output to the console, searching the classpath from its root for features and executing all tests found in packages.

It is also possible to filter exactly which features get executed by using the test-only task. To do this, specify the CucumberSuite that you defined above as the test to run and then use either the tag or name approach already described as the test arguments:

      it:test-only mypackage.CucumberSuite -- @demo
      it:test-only mypackage.CucumberSuite -- "User admin"

## Writing Features ##
Features are written in text format and are placed in .feature files inside the 'src/test/resources' directory. For more info on writing features please see the [Cucumber](http://cukes.info) website.
For example:

    Feature: Cucumber
      In order to implement BDD in my Scala project
      As a developer
      I want to be able to run Cucumber from with SBT

      Scenario: Execute feature with console output
        Given A SBT project
        When I run the cucumber goal
        Then Cucumber is executed against my features and step definitions

The location of the features can be changed by overriding a plugin setting (see below) - but only when using the cucumber task.

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

    addSbtPlugin("templemore" % "sbt-cucumber-plugin" % "0.7.2")

### Basic Configuration ###
To add the cucumber plugin settings to a basic project, just add the following to the build.sbt file:

    seq(cucumberSettings : _*)

The testProjects/testProject in the plugin source repository shows this configuration.

#### Running as a test framework ####
If you wish to support cucumber running as a test framework (via the test task) then use this alternative settings group instead:

    seq(cucumberSettingsWithTestPhaseIntegration : _*)

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

Note that because SBT runs builds for different projects in parallel it will often try to run cucumber/test goals for multiple modules at once. This results in interleaving of the output written to the console. For multi-module builds using the cucumber task it is best to change the output settings to generate reports rather than console output. Unfortunately this is not possible when using the test task, so the recommended approach is to run the test task against each project module individually.

#### Running as a test framework ####
If you wish to support cucumber running as a test framework (via the test task) then the following settings should be placed in the build file instead:

    lazy val myProject = Project ("my-project", file ("."),
                                  settings = Defaults.defaultSettings ++ 
                                             CucumberPlugin.cucumberSettingsWithTestPhaseIntegration)

#### Running as an integration test framework ####
If you wish to support cucumber running as an integration test framework (via the it:test task) then use this alternative settings group instead:

    lazy val myProject = Project ("my-project", file ("."),
                                  settings = Defaults.defaultSettings ++ 
                                             CucumberPlugin.cucumberSettingsWithIntegrationTestPhaseIntegration)
                                   .configs(IntegrationTest)
                                   .settings(Defaults.itSettings : _*)

This will ensure the necessary jars and test frameworks are installed in the IntegrationTest config as opposed to the normal Test config. For more information about setting up an IntegrationTest config see the testProjects/integrationTestIntegrationProject or http://www.scala-sbt.org/release/docs/Detailed-Topics/Testing.

## Customisation ##
The plugin supports a number of customisations and settings. Note that these setting customisations only apply to running using the standalone 'cucumber' task. Running cucumber as a test framework does not support any customisation options.

The following settings can be modified to change the behaviour of the plugin:

### Cucumber Settings ###
* cucumberFeaturesLocation - The location of the cucumber features directory within the projects. Defaults to the String "classpath:". Can be set to a specific classpath location or a full file path
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

### 0.7.2 ###

Fix for issue #16. Failing cucumber tests now cause the build to report an error.

### 0.7.1 ###

Fix for issue #15. The templemore repository is now added as a resolver to the project when the plugin is added.
Fix for issue #14. It is now possible to run cucumber within the IntegrationTest config.

### 0.7.0 ###

Significant refactor to support running cucumber via the standalone 'cucumber' task or integrated as test framework that can run as part of the standard SBT 'test' task.
The default location for feature files is now on the classpath, requiring features to be placed in src/test/resources.

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
