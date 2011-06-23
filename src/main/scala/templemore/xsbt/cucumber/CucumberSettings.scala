package templemore.xsbt.cucumber

import java.io.File

/**
 * @author Chris Turner
 */
case class CucumberSettings(featuresDir: File,
                            requiredPath: List[File],
                            options: List[String])
