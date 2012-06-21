package templemore.xsbt.cucumber

import sbt._
import std.TaskStreams

/**
 * @author Chris Turner
 */
trait CucumberIntegration {

  protected def optionsForMode(mode: CucumberMode,
                               htmlReportFile: File) = {
    htmlReportFile.getParentFile.mkdirs()
    mode match {
      case Developer => List[String]("--format", "pretty")
      case HtmlReport => List[String]("--format", "html", "--out", htmlReportFile.getPath)
      case _ => List[String]("--format", "pretty")
    }
  }

  protected def testWithCucumber(args: Seq[String],
                                 cucumberSettings: CucumberSettings,
                                 cucumberOptions: CucumberOptions,
                                 s: TaskStreams[_]) = {
    val log = s.log

    if ( cucumberOptions.featuresPresent ) {
      log.debug("Cucumber Settings: %s".format(cucumberSettings))
      log.debug("Cucumber Options: %s".format(cucumberOptions))

      runCucumber(args, cucumberSettings, cucumberOptions, log)
    }
    else {
      log.info("No features directory found. Skipping for curent project.")
      0
    }
  }

  private def runCucumber(args: Seq[String],
                          cucumberSettings: CucumberSettings,
                          cucumberOptions: CucumberOptions,
                          log: Logger) = {
    def tagsFromArgs(args: Seq[String]) = args.filter(isATag).toList
    def namesFromArgs(args: Seq[String]) = args.filter(isNotATag).toList

    def isATag(arg: String) = arg.startsWith("@") || arg.startsWith("~")
    def isNotATag(arg: String) = !isATag(arg)

    log.info("Running cucumber...")
    cucumberOptions.beforeFunc()
    val cucumber = Cucumber(cucumberSettings.classpath, cucumberSettings.outputStrategy,
                            Some(cucumberSettings.maxMemory), Some(cucumberSettings.maxPermGen))
    val result = cucumber.cuke(cucumberOptions.featuresDir, cucumberOptions.basePackage,
                               cucumberOptions.options, tagsFromArgs(args), namesFromArgs(args))
    cucumberOptions.afterFunc()
    result
  }
}