package templemore.sbt.cucumber

import org.scalatools.testing._

/**
 * Framework implementation that allows cucumber to be run as part of sbt's
 * standard 'test' phase.
 *
 * @author Chris Turner
 */
class CucumberFramework extends Framework {
  val name = "cucumber"
  val tests = Array[Fingerprint](CucumberRunOnceFingerprint) 

  def testRunner(testClassLoader: ClassLoader, loggers: Array[Logger]) = {
    //TODO
    println("%%% Creating a new runner...")
    new CucumberRunner()
  }
}

class CucumberRunner extends Runner2 {
  def run(testClassName: String, fingerprint: Fingerprint, eventHandler: EventHandler, args: Array[String]) = {
    println("%%% CUCUMBER RUNNER")
    println("%%% Running test class: " + testClassName)
    println("%%%  with fingerprint: " + fingerprint)
    println("%%%  with eventHandler: " + eventHandler)
    println("%%%  with args: " + args.mkString(", "))
    //TODO
  }  
}

object CucumberRunOnceFingerprint extends SubclassFingerprint {
  val isModule = false
  val superClassName = classOf[RunCucumber].getName
}  

trait RunCucumber
