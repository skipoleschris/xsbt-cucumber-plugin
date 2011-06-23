package templemore.jruby

import java.io.File
import sbt.{StdoutOutput, Fork, OutputStrategy}

/**
 * @author Chris Turner
 */
private[jruby] trait JRuby {

  protected def jRubyHome: File
  protected def gemDir: File
  protected def classpath: List[File]
  protected def javaOpts: List[String]
  protected def outputStrategy: OutputStrategy

  protected def maxMemory = "256M"
  protected def maxPermGen = "64M"

  protected def jruby(arguments: List[String]): Int = {
    val args = jvmArgs ++ ("org.jruby.Main" :: arguments)
    println("Calling JRuby: " + args.mkString(" "))
    Fork.java(None, args, None, jRubyEnv, outputStrategy)
  }

  private def jRubyEnv = Map("GEM_PATH" -> gemDir.getPath,
                             "HOME" -> jRubyHome.getPath)

  private def jvmArgs = "-classpath" :: makeClasspath(classpath) ::
                        ("-Xmx%s" format maxMemory) :: ("-XX:MaxPermSize=%s" format maxPermGen) :: javaOpts

  protected def makeClasspath(pathElements: List[File]) = pathElements.map(_.getPath).mkString(File.pathSeparator)
  protected def makeOptionsList(options: List[String], flag: String) = options flatMap(List(flag, _))
}