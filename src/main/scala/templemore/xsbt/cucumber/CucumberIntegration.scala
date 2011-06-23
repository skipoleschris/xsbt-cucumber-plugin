package templemore.xsbt.cucumber

import sbt._
import std.TaskStreams
import templemore.jruby.GemInstaller
import templemore.jruby.Cucumber

/**
 * @author Chris Turner
 */
trait CucumberIntegration {

  protected def optionsForMode(mode: CucumberMode,
                               htmlReportFile: File,
                               pdfReportFile: File) = {
    htmlReportFile.getParentFile.mkdirs()
    pdfReportFile.getParentFile.mkdirs()
    mode match {
      case Developer => List[String]("--format", "pretty")
      case HtmlReport => List[String]("--format", "html", "--out", htmlReportFile.getPath)
      case PdfReport => List[String]("--format", "pdf", "--out", pdfReportFile.getPath)
      case _ => List[String]("--format", "pretty", "--no-source", "--no-snippets")
    }
  }

  protected def testWithCucumber(args: Seq[String],
                                 jRubySettings: JRubySettings,
                                 gemSettings: GemSettings,
                                 cukeSettings: CucumberSettings,
                                 s: TaskStreams[_]) = {
    val log = s.log
    log.debug("JRuby Settings: %s" format(jRubySettings))
    log.debug("Gem Settings: %s" format(gemSettings))
    log.debug("Cucumber Options: %s" format(cukeSettings))

    installGems(jRubySettings, gemSettings, log)
    runCucumber(args, jRubySettings, cukeSettings, log)
  }

  private def installGems(jRubySettings: JRubySettings,
                          gemSettings: GemSettings,
                          log: Logger) = {
    log.info("Installing any missing Gems...")
    val gemInstaller = GemInstaller(jRubySettings.jRubyHome, jRubySettings.gemDir,
                                    jRubySettings.classpath, jRubySettings.outputStrategy)
    gemSettings.gems.foreach(gemInstaller.installGem(_, gemSettings.forceReload))
  }

  private def runCucumber(args: Seq[String],
                          jRubySettings: JRubySettings,
                          cukeSettings: CucumberSettings,
                          log: Logger) = {
    def tagsFromArgs(args: Seq[String]) = args.filter(isATag).toList
    def namesFromArgs(args: Seq[String]) = args.filter(isNotATag).toList

    def isATag(arg: String) = arg.startsWith("@") || arg.startsWith("~")
    def isNotATag(arg: String) = !isATag(arg)

    log.info("Running cucumber...")
    val cucumber = Cucumber(jRubySettings.jRubyHome, jRubySettings.gemDir,
                            jRubySettings.classpath, jRubySettings.outputStrategy,
                            Some(jRubySettings.maxMemory), Some(jRubySettings.maxPermGen))
    cucumber.cuke(cukeSettings.featuresDir, cukeSettings.requiredPath,
                  cukeSettings.options, tagsFromArgs(args), namesFromArgs(args))
  }
}