package templemore.sbt.cucumber

import sbt._
import std.TaskStreams
import templemore.sbt.util._

/**
 * Provides the actual integration with cucumber jvm. Capable of launching 
 * cucumber as both a forked JVM and within the current JVM process.
 *
 * @author Chris Turner
 * @author RandomCoder
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

  /*
   * The options that are supported by the plugin.
   * This excludes options that are set in other places such as formatting
   * and dotcucumber etc.
   *
   * This is essentially a list of the parameter-less options supported by the
   * `cucumber-jvm` `cucumber.runtime.RuntimeOptions` class
   *
   * The `--no-xxx` version of the options are not included as they are not enabled
   * by default and are therefore not really necessary.
   */
  private val supportedOptions = Seq("-d",
                                     "--dry-run",
                                     "-s",
                                     "--strict",
                                     "-m",
                                     "--monochrome")


  private def runCucumber(args: Seq[String],
                          jvmSettings: JvmSettings,
                          options: Options,
                          output: Output,
                          log: Logger) = {
    def tagsFromArgs = args.filter(isATag).toList
    def optsFromArgs = args.filter(isAnOption).toList
    def namesFromArgs = args.filter(isAName).toList

    def isAnOption(arg: String) = supportedOptions.contains(arg)
    def isATag(arg: String) = arg.startsWith("@") || arg.startsWith("~@")
    def isAName(arg:String) = !isATag(arg) && !isAnOption(arg)

    log.info("Running cucumber...")
    options.beforeFunc()
    val result = launchCucumberInSeparateJvm(jvmSettings, options, output, tagsFromArgs, namesFromArgs, optsFromArgs)
    options.afterFunc()
    result
  }

  private def launchCucumberInSeparateJvm(jvmSettings: JvmSettings, 
                                          options: Options,
                                          output: Output,
                                          tags: List[String], 
                                          names: List[String],
                                          cucumberOptions: List[String]): Int = {
    def makeOptionsList(options: List[String], flag: String) = options flatMap(List(flag, _))

    val cucumberParams = ("--glue" :: options.basePackage :: Nil) ++
                         options.extraOptions ++ 
                         output.options ++
                         makeOptionsList(tags, "--tags") ++ 
                         makeOptionsList(names, "--name") ++
                         cucumberOptions ++
                         (options.featuresLocation :: Nil)
    JvmLauncher(jvmSettings).launch(cucumberParams)
  }
}
