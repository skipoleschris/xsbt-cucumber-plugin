package templemore.sbt.util

import sbt._
import java.io.File
import sbt.OutputStrategy

/**
 * Launches a new JVM with the given options.
 *
 * @author Chris Turner
 */
case class JvmLauncher(settings: JvmSettings) {

  private val maxMemory = settings.overrideMaxMemory.getOrElse("256M")
  private val maxPermGen = settings.overrideMaxPermGen.getOrElse("64M")

  private val jvmArgs = ("-classpath" :: makeClasspath ::
                        ("-Xmx%s" format maxMemory) :: ("-XX:MaxPermSize=%s" format maxPermGen) :: Nil) ++
                        makeSystemProperties ++ settings.jvmOptions

  def launch(params: List[String]): Int = {
    val args = jvmArgs ++ (settings.mainClass :: params)
    settings.outputStrategy.asInstanceOf[LoggedOutput].logger.debug(args mkString " ")
    Fork.java(None, args, None, Map.empty[String, String], settings.outputStrategy)
  }

  protected def makeClasspath = settings.classpath map (_.getPath) mkString File.pathSeparator
  protected def makeSystemProperties = settings.systemProperties.toList map (entry => "-D%s=%s".format(entry._1, entry._2))
}

/**
 * The options to pass to the JVM.
 *
 * @author Chris Turner
 */
case class JvmSettings(classpath: List[File],
                       mainClass: String,
                       outputStrategy: OutputStrategy,
                       systemProperties: Map[String, String],
                       jvmOptions: List[String],
                       overrideMaxMemory: Option[String],
                       overrideMaxPermGen: Option[String])
