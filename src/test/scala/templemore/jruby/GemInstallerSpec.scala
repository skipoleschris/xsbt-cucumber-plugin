package templemore.jruby

import sbt._
import org.scalatest.matchers.MustMatchers
import sbt.StdoutOutput
import java.io.File
import org.scalatest.{BeforeAndAfterEach, FlatSpec}

/**
 * @author Chris Turner
 */
class GemInstallerSpec extends FlatSpec with MustMatchers with BeforeAndAfterEach {

  private val jRubyHome = new File(System.getProperty("user.dir"), "target")
  private val gemDir = new File(jRubyHome, "gems")
  private val jRubyJar = new File(System.getProperty("user.home")) / ".ivy2" / "cache" / "org.jruby" / "jruby-complete" / "jars" / "jruby-complete-1.6.1.jar"

  private val gemName = "json"
  private val gemVersion = "1.5.3"

  override def beforeEach() {
    IO.delete(gemDir)
    IO.createDirectory(gemDir)
  }

  "A gem installer" should "install the test gem" in {
    installTestGem()
    gemDirectory.exists() must be(true)
  }

  it should "not install the test gem if it is already present" in {
    installTestGem()
    val timestamp = gemFile.lastModified()
    installTestGem()
    gemFile.lastModified() must be(timestamp)
  }

  it should "force install the test gem even if it is already present" in {
    installTestGem()
    val timestamp = gemFile.lastModified()
    installTestGem(true)
    gemFile.lastModified() must not be(timestamp)
  }

  private def gemDirectory = gemDir / "gems" / "%s-%s-java".format(gemName, gemVersion)
  private def gemFile = gemDirectory / "lib" / "json.rb"

  private def installTestGem(force: Boolean = false) {
    val installer = new GemInstaller(jRubyHome, gemDir,
                                     List(jRubyJar),
                                     StdoutOutput)
    val gem = Gem(gemName, Some(gemVersion), Some("http://rubygems.org/"), Some("java"))
    installer.installGem(gem, force)
  }
}