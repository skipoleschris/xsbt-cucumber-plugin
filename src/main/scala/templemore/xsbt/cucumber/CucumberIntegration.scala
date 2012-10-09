package templemore.xsbt.cucumber

import sbt._
import std.TaskStreams

/**
 * @author Chris Turner
 */
trait CucumberIntegration {

  protected def testWithCucumber(args: Seq[String],
                                 cucumberSettings: CucumberSettings,
                                 cucumberOptions: CucumberOptions,
                                 cucumberOutput: CucumberOutput,
                                 s: TaskStreams[_]) = {
    val log = s.log

    if ( cucumberOptions.featuresPresent ) {
      log.debug("Cucumber Settings: %s".format(cucumberSettings))
      log.debug("Cucumber Options: %s".format(cucumberOptions))
      log.debug("Cucumber Output: %s".format(cucumberOutput))

      runCucumber(args, cucumberSettings, cucumberOptions, cucumberOutput, log)
    }
    else {
      log.info("No features directory found. Skipping for curent project.")
      0
    }
  }

  private def runCucumber(args: Seq[String],
                          cucumberSettings: CucumberSettings,
                          cucumberOptions: CucumberOptions,
                          cucumberOutput: CucumberOutput,
                          log: Logger) = {
    def tagsFromArgs(args: Seq[String]) = args.filter(isATag).toList
    def namesFromArgs(args: Seq[String]) = args.filter(isNotATag).toList

    def isATag(arg: String) = arg.startsWith("@") || arg.startsWith("~")
    def isNotATag(arg: String) = !isATag(arg)

    log.info("Running cucumber...")
    cucumberOptions.beforeFunc()
    val cucumber = Cucumber(cucumberSettings.classpath, cucumberSettings.outputStrategy,
                            cucumberSettings.systemProperties, cucumberSettings.jvmOptions,
                            Some(cucumberSettings.maxMemory), Some(cucumberSettings.maxPermGen))
    val result = cucumber.cuke(cucumberOptions.featuresDir, cucumberOptions.basePackage,
                               cucumberOptions.options ++ cucumberOutput.options, tagsFromArgs(args), namesFromArgs(args))
    cucumberOptions.afterFunc()
    result
  }
}