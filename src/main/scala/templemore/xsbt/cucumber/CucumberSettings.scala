package templemore.xsbt.cucumber

import java.io.File

/**
 * @author Chris Turner
 */
case class CucumberSettings(featuresDir: File,
                            requiredPath: File,
                            options: List[String],
                            beforeFunc: () => Unit,
                            afterFunc: () => Unit) {

  def featuresPresent = featuresDir.exists
}
