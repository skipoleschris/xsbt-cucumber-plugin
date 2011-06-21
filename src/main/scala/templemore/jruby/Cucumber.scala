package templemore.jruby

import java.io.File
import sbt.OutputStrategy
import templemore.path.Path

/**
 * @author Chris Turner
 */
case class Cucumber(jRubyHome: File,
                    gemDir: File,
                    classpath: List[String],
                    outputStrategy: OutputStrategy,
                    overrideMaxMemory: Option[String] = None,
                    overrideMaxPermGen: Option[String] = None) extends JRuby {

  override protected def maxMemory = overrideMaxMemory.getOrElse(super.maxMemory)
  override protected def maxPermGen = overrideMaxPermGen.getOrElse(super.maxPermGen)

  protected val javaOpts = List("-Dcuke4duke.objectFactory=cuke4duke.internal.jvmclass.PicoFactory")

  def cuke(featuresDir: File, testCompileClasspath: List[String], options: List[String] = List(),
           tags: List[String] = List(), names: List[String] = List()): Int = {
    import Path._
    def cuke4duke = (gemDir / "bin" / "cuke4duke").toString

    jruby( (cuke4duke :: featuresDir.getPath :: "--require" :: makeClasspath(testCompileClasspath) :: "--color" :: Nil) ++
           options ++ makeOptionsList(tags, "--tags") ++ makeOptionsList(names, "--names") )
  }
}
