package templemore.jruby

import java.io.File
import sbt.{StdoutOutput, Fork, OutputStrategy}

/**
 * @author Chris Turner
 */
private[jruby] trait JRuby {

  protected def jRubyHome: File
  protected def gemDir: File
  protected def classpath: List[String]
  protected def javaOpts: List[String]
  protected def outputStrategy: OutputStrategy

  protected def jruby(arguments: List[String]): Int = {
    val args = jvmArgs ++ ("org.jruby.Main" :: arguments)
    Fork.java(None, args, None, jRubyEnv, outputStrategy)
  }

  private def jRubyEnv = Map("GEM_PATH" -> gemDir.getPath,
                             "HOME" -> jRubyHome.getPath)

  private def jvmArgs = "-classpath" :: classpath.mkString(System.getProperty("path.separator")) :: javaOpts
}