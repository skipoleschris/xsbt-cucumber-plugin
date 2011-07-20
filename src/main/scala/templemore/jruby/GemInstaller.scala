package templemore.jruby

import sbt._
import scala.Some
import sbt.OutputStrategy
import java.io.File

/**
 * @author Chris Turner
 */
case class Gem(name: String,
               version: Option[String],
               source: Option[String],
               classifier: Option[String] = None) {

  override def toString = version match {
    case None => name
    case Some(v) => classifier match {
      case None => "%s-%s" format(name, v)
      case Some(c) => "%s-%s-%s" format(name, v, c)
    }
  }

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

class GemInstaller(val jRubyHome: File,
                   val gemDir: File,
                   val classpath: List[File],
                   val outputStrategy: OutputStrategy) extends JRuby {
  IO.createDirectory(gemDir)

  protected val javaOpts = List[String]()

  def installGem(gem: Gem, force: Boolean = false): Int = {
    def present(gem: Gem) = (gemDir / "gems" / gem.toString).exists

    if ( !present(gem) || force )
      jruby(("-S" :: "gem" :: "install" :: "--no-ri" :: "--no-rdoc" ::
            "--install-dir" :: gemDir.getPath :: Nil) ++ gem.toArgs)
    else 0
  }
}
