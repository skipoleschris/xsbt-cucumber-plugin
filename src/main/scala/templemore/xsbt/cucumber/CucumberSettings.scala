package templemore.xsbt.cucumber

import java.io.File

/**
 * @author Chris Turner
 */
case class CucumberSettings(featuresDir: File,
                            requiredPath: List[String],
                            options: List[String],
                            tags: List[String],
                            names: List[String])
