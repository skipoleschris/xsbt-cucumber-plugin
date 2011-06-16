package templemore.jruby

import org.scalatest.matchers.MustMatchers
import sbt.StdoutOutput
import java.io.File
import org.scalatest.{BeforeAndAfterEach, FlatSpec}
import scala.Some

/**
 * @author Chris Turner
 */
class GemInstallerSpec extends FlatSpec with MustMatchers with BeforeAndAfterEach {

  private val jRubyHome = new File(System.getProperty("user.dir"), "target")
  private val gemDir = new File(jRubyHome, "gems")

  override def beforeEach() {
    if ( gemDir.exists ) deleteAllFiles(gemDir)
    else gemDir.mkdirs()
  }


  "A gem installer" should "install the cucumber gem" in {
    val installer = new GemInstaller(jRubyHome, gemDir,
                                     List("/Users/chris/.ivy2/cache/org.jruby/jruby-complete/jars/jruby-complete-1.6.1.jar"),
                                     StdoutOutput)
    installer.installGem(Gem("cucumber", Some("0.10.6"), Some("http://rubygems.org/")))

    new File(gemDir, "gems/cucumber-0.10.6").exists() must be(true)
  }

  private def deleteAllFiles(dir: File): Unit = dir.listFiles.foreach { f =>
    if ( f.isDirectory ) deleteAllFiles(f)
    f.delete()
  }
}