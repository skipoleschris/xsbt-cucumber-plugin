package templemore.xsbt.cucumber

import sbt._
import java.io.File
import sbt.OutputStrategy

/**
 * @author Chris Turner
 */
case class Cucumber(classpath: List[File],
                    outputStrategy: OutputStrategy,
                    overrideMaxMemory: Option[String] = None,
                    overrideMaxPermGen: Option[String] = None) {

  private val maxMemory = overrideMaxMemory.getOrElse("256M")
  private val maxPermGen = overrideMaxPermGen.getOrElse("64M")

  private val jvmArgs = "-classpath" :: makeClasspath(classpath) ::
                        ("-Xmx%s" format maxMemory) :: ("-XX:MaxPermSize=%s" format maxPermGen) :: Nil

  def cuke(featuresDir: File, requirePath: File, options: List[String] = List(),
           tags: List[String] = List(), names: List[String] = List()): Int = {

    val cucumberParams = ("--glue" :: requirePath.getPath :: Nil) ++
                         options ++ makeOptionsList(tags, "--tags") ++ makeOptionsList(names, "--name") ++
                         (featuresDir.getPath :: Nil)

    val args = jvmArgs ++ ("cucumber.cli.Main" :: cucumberParams)
    outputStrategy.asInstanceOf[LoggedOutput].logger.debug(args mkString " ")
    Fork.java(None, args, None, Map.empty[String, String], outputStrategy)
  }

  protected def makeClasspath(pathElements: List[File]) = pathElements.map(_.getPath).mkString(File.pathSeparator)
  protected def makeOptionsList(options: List[String], flag: String) = options flatMap(List(flag, _))
}
