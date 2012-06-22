package templemore.xsbt.cucumber

import java.io.File
import sbt.OutputStrategy

/**
 * @author Chris Turner
 */
case class CucumberSettings(maxMemory: String,
                            maxPermGen: String,
                            classpath: List[File],
                            outputStrategy: OutputStrategy)

