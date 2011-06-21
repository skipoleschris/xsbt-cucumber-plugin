package templemore.path

import java.io.File

/**
 * @author Chris Turner
 */
object Path {
  implicit def fileToPath(file: File) = Path(file)
}
case class Path(file: File) {
  def toFile = file
  override def toString = file.getPath
  def exists = file.exists()
  def /(dir: String) = Path(new File(file, dir))
}