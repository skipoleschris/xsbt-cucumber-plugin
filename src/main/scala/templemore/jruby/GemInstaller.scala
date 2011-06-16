package templemore.jruby

import scala.Some
import sbt.{OutputStrategy, Fork}
import java.io.File

/**
 * @author Chris Turner
 */
case class Gem(name: String, version: Option[String], source: Option[String]) {

  def toArgs = (name :: versionArgs) ++ sourceArgs

  private def versionArgs = version match {
    case Some(v) => "--version" :: v :: Nil
    case _ => Nil
  }

  private def sourceArgs = source match {
    case Some(s) => "--source" :: s :: Nil
    case _ => Nil
  }
}

case class GemInstaller(jRubyHome: File,
                        gemDir: File,
                        classpath: List[String],
                        outputStrategy: OutputStrategy) extends JRuby {
  gemDir.mkdirs()

  protected val javaOpts = List[String]()

  def installGem(gem: Gem): Int =
    jruby(("-S" :: "gem" :: "install" :: "--no-ri" :: "--no-rdoc" ::
          "--install-dir" :: gemDir.getPath :: Nil) ++ gem.toArgs)
}
