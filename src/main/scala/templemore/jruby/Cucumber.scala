package templemore.jruby

import sbt._
import java.io.File
import sbt.OutputStrategy

/**
 * @author Chris Turner
 */
case class Cucumber(jRubyHome: File,
                    gemDir: File,
                    classpath: List[File],
                    outputStrategy: OutputStrategy,
                    overrideMaxMemory: Option[String] = None,
                    overrideMaxPermGen: Option[String] = None) extends JRuby {

  override protected def maxMemory = overrideMaxMemory.getOrElse(super.maxMemory)
  override protected def maxPermGen = overrideMaxPermGen.getOrElse(super.maxPermGen)

  protected val javaOpts = List("-Dcuke4duke.objectFactory=cuke4duke.internal.jvmclass.PicoFactory")

  def cuke(featuresDir: File, testCompileClasspath: List[File], options: List[String] = List(),
           tags: List[String] = List(), names: List[String] = List()): Int = {
    def cuke4duke = (gemDir / "bin" / "cuke4duke").getPath

//    jruby( (cuke4duke :: featuresDir.getPath :: "--require" :: makeClasspath(testCompileClasspath) :: "--color" :: Nil) ++
//           options ++ makeOptionsList(tags, "--tags") ++ makeOptionsList(names, "--names") )
    jruby( (cuke4duke :: "--help" :: Nil ) )
  }
}
