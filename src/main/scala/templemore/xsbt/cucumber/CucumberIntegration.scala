package templemore.xsbt.cucumber

import sbt._
import std.TaskStreams

/**
 * @author Chris Turner
 */
trait CucumberIntegration {

  protected def optionsForReporting(htmlReportDir: Option[File],
                                    jsonReportFile: Option[File],
                                    junitReportFile: Option[File]): List[String] = {
    (Some("--format" :: "pretty" :: Nil) ::
     (htmlReportDir map { dir => 
       dir.mkdirs()
       "--format" :: "html:%s".format(dir.getPath) :: Nil }) ::
     (jsonReportFile map { file => 
       file.getParentFile.mkdirs()
       "--format" :: "json-pretty:%s".format(file.getPath) :: Nil }) ::
     (junitReportFile map { file => 
       file.getParentFile.mkdirs()
       "--format" :: "junit:%s".format(file.getPath) :: Nil }) :: Nil).flatten.flatten
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