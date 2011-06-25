package templemore.xsbt.cucumber

import java.io.File
import sbt.OutputStrategy

/**
 * @author Chris Turner
 */
case class JRubySettings(jRubyHome: File,
                         gemDir: File,
                         classpath: List[File],
                         maxMemory: String,
                         maxPermGen: String,
                         outputStrategy: OutputStrategy)
