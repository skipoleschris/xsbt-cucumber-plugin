package templemore.sbt.cucumber

import org.scalatools.testing._

import java.util.Properties

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
    loggers foreach (_.debug("Creating a new Cucumber test runner"))
    new CucumberRunner(testClassLoader, loggers)
  }
}

class CucumberRunner(testClassLoader: ClassLoader, loggers: Array[Logger]) extends Runner2 {
  private val cucumber = new CucumberLauncher(debug = logDebug, error = logError)

  def run(testClassName: String, fingerprint: Fingerprint, eventHandler: EventHandler, args: Array[String]) = try {
    val arguments = Array("--glue", "", "--format", "pretty", "classpath:")

    cucumber(arguments, testClassLoader) match {
      case 0 => 
        logDebug("Cucumber tests completed successfully")
        eventHandler.handle(SuccessEvent(testClassName))
      case _ => 
        logDebug("Failure while running Cucumber tests")
        eventHandler.handle(FailureEvent(testClassName))
    }
  } catch {
    case e => eventHandler.handle(ErrorEvent(testClassName, e))
  }

  private def logError(message: String) = loggers foreach (_ error message)
  private def logDebug(message: String) = loggers foreach (_ debug message)

  case class SuccessEvent(testName: String) extends Event {
    val description = "Cucumber tests completed successfully."
    val result = Result.Success
    val error: Throwable = null
  }

  case class FailureEvent(testName: String) extends Event {
    val description = "There were test failures (or undefined/pending steps)."
    val result = Result.Failure
    val error: Throwable = null
  }

  case class ErrorEvent(testName: String, error: Throwable) extends Event {
    val description = "An error occurred while running Cucumber."
    val result = Result.Error
  }
}

object CucumberRunOnceFingerprint extends SubclassFingerprint {
  val isModule = false
  val superClassName = classOf[RunCucumber].getName
}  

trait RunCucumber
