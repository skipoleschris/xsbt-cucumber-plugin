package templemore.xsbt.cucumber

import sbt._
import java.io.File
import sbt.OutputStrategy

/**
 * @author Chris Turner
 */
case class Cucumber(classpath: List[File],
                    mainClass: String,
                    outputStrategy: OutputStrategy,
                    systemProperties: Map[String, String],
                    jvmOptions: List[String],
                    overrideMaxMemory: Option[String] = None,
                    overrideMaxPermGen: Option[String] = None) {

  private val maxMemory = overrideMaxMemory.getOrElse("256M")
  private val maxPermGen = overrideMaxPermGen.getOrElse("64M")

  private val jvmArgs = ("-classpath" :: makeClasspath ::
                        ("-Xmx%s" format maxMemory) :: ("-XX:MaxPermSize=%s" format maxPermGen) :: Nil) ++
                        makeSystemProperties ++ jvmOptions

  def cuke(featuresDir: File, basePackage: String, options: List[String] = List(),
           tags: List[String] = List(), names: List[String] = List()): Int = {

    val cucumberParams = ("--glue" :: basePackage :: Nil) ++
                         options ++ makeOptionsList(tags, "--tags") ++ makeOptionsList(names, "--name") ++
                         (featuresDir.getPath :: Nil)

    val args = jvmArgs ++ (mainClass :: cucumberParams)
    outputStrategy.asInstanceOf[LoggedOutput].logger.debug(args mkString " ")
    Fork.java(None, args, None, Map.empty[String, String], outputStrategy)
  }

  protected def makeClasspath = classpath.map(_.getPath).mkString(File.pathSeparator)
  protected def makeOptionsList(options: List[String], flag: String) = options flatMap(List(flag, _))
  protected def makeSystemProperties = systemProperties.toList map (entry => "-D%s=%s".format(entry._1, entry._2))
}
