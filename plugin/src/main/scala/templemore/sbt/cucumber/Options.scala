package templemore.sbt.cucumber

import java.io.File

/**
 * The options to pass to cucumber.
 *
 * @author Chris Turner
 */
case class Options(featuresDir: File,
                   basePackage: String,
                   extraOptions: List[String],
                   beforeFunc: () => Unit,
                   afterFunc: () => Unit) {

  def featuresPresent = featuresDir.exists
}
