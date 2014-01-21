package templemore.sbt.cucumber

import sbt._
import std.TaskStreams
import templemore.sbt.util._

/**
 * Provides the actual integration with cucumber jvm. Capable of launching 
 * cucumber as both a forked JVM and within the current JVM process.
 *
 * @author Chris Turner
 */
trait Integration {

  protected def cuke(args: Seq[String],
                     jvmSettings: JvmSettings,
                     options: Options,
                     output: Output,
                     s: TaskStreams[_]) = {
    val log = s.log

    if ( options.featuresPresent ) {
      log.debug("JVM Settings: %s".format(jvmSettings))
      log.debug("Cucumber Options: %s".format(options))
      log.debug("Cucumber Output: %s".format(output))

      runCucumber(args, jvmSettings, options, output, log)
    }
    else {
      log.info("No features directory found. Skipping for curent project.")
      0
    }
  }

  private def runCucumber(args: Seq[String],
                          jvmSettings: JvmSettings,
                          options: Options,
                          output: Output,
                          log: Logger): Int = {
    def isATag(arg: String) = arg.startsWith("@") || arg.startsWith("~")
    def isAFeature(arg: String) = arg.indexOf(":") != -1
    def isAName(arg: String) = !isATag(arg) && !isAFeature(arg)

    def prepandFeatureLocation(features: List[String]) = features.map((feature: String) => options.featuresLocation + feature)

    val tagsFromArgs = args.filter(isATag).toList
    val namesFromArgs = args.filter(isAName).toList
    var featuresFromArgs = prepandFeatureLocation(args.filter(isAFeature).toList)

    if(options.rerun) {
      val rerun_text = scala.io.Source.fromFile(output.rerunReportFile).mkString
      if(rerun_text.isEmpty) {
        log.error("Rerun file (" + output.rerunReportFile + ") is empty")
        return -1
      }
      featuresFromArgs ++= prepandFeatureLocation(rerun_text.replaceAll("([:0-9]+) ", "$1|").split("\\|").toList)
    }

    var info = "Running cucumber"
    if(!tagsFromArgs.isEmpty) info += " tags: " + tagsFromArgs.mkString(", ")
    if(!featuresFromArgs.isEmpty) info += " features: " + featuresFromArgs.mkString(", ")
    if(!namesFromArgs.isEmpty) info += " names: " + namesFromArgs.mkString(", ")
    log.info(info)
    options.beforeFunc()
    val result = launchCucumberInSeparateJvm(log, jvmSettings, options, output, tagsFromArgs, namesFromArgs, featuresFromArgs)
    options.afterFunc()
    result
  }

  private def launchCucumberInSeparateJvm(log: Logger, jvmSettings: JvmSettings,
                                          options: Options,
                                          output: Output,
                                          tags: List[String], 
                                          names: List[String],
                                          features: List[String]): Int = {
    def makeOptionsList(options: List[String], flag: String) = options flatMap(List(flag, _))
    def prependOption(name: String)(on: Boolean, list: List[String]) = if (on) ("--%s".format(name) :: list) else list

    val monochrome = prependOption("monochrome")_
    val strict = prependOption("strict")_
    val dryRun = prependOption("dry-run")_
    def additionalOptions = monochrome(options.monochrome, strict(options.strict, dryRun(options.dryRun, options.extraOptions)))

    val cucumberParams = ("--glue" :: options.basePackage :: Nil) ++
                         additionalOptions ++ 
                         output.options ++
                         makeOptionsList(tags, "--tags") ++
                         makeOptionsList(names, "--name") ++
                         features ++
                         (if (features.isEmpty) (options.featuresLocation :: Nil) else Nil)
//    log.info(jvmSettings.toString())
//    log.info(cucumberParams.toString())
    JvmLauncher(jvmSettings).launch(cucumberParams)
  }

}
