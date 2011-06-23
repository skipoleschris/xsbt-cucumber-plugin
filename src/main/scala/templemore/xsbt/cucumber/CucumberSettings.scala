package templemore.xsbt.cucumber

import java.io.File

/**
 * @author Chris Turner
 */
case class CucumberSettings(featuresDir: File,
                            requiredPath: File,
                            options: List[String]) {

  def featuresPresent = featuresDir.exists
}
