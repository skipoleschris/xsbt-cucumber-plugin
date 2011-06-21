package templemore.path

import org.scalatest.FlatSpec
import java.io.File
import org.scalatest.matchers.MustMatchers
import java.security.SecurityPermission

/**
 * @author Chris Turner
 */
class PathSpec extends FlatSpec with MustMatchers {

  import Path._

  "Path utilities" should "contruct a path from a file" in {
    val expected = new File(currentDir, "dir")
    val path: Path = expected
    path must be(Path(new File(currentDir, "dir")))
    path.toFile must be(expected)
  }

  it should "append a string onto a path" in {
    val path: Path = new File(currentDir) / "dir"
    path.toFile must be(new File(currentDir, "dir"))
  }

  it should "append multiple strings onto a path" in {
    val path: Path = new File(currentDir) / "dir" / "child" / "dir"
    val sep = File.separator
    path.toFile must be(new File(currentDir + sep + "dir" + sep + "child" + sep + "dir"))
  }

  private def currentDir = System.getProperty("user.dir")
}
