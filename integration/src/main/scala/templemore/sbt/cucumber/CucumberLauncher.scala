package templemore.sbt.cucumber

import scala.collection.JavaConverters._
import java.lang.reflect.InvocationTargetException
import java.util.Properties

import cucumber.runtime.Runtime
import cucumber.runtime.RuntimeOptions
import cucumber.runtime.snippets.SummaryPrinter
import cucumber.runtime.model.CucumberFeature
import gherkin.formatter.Formatter
import gherkin.formatter.Reporter

class CucumberLauncher(debug: (String) => Unit, error: (String) => Unit) {
  
  private val MultiLoaderClassName = "cucumber.runtime.io.MultiLoader"
  private val MultiLoaderClassName_1_0_9 = "cucumber.io.MultiLoader"

  def apply(cucumberArguments: Array[String],
            testClassLoader: ClassLoader): Int = {
    debug("Cucumber arguments: " + cucumberArguments.mkString(" "))
    val runtime = buildRuntime(System.getProperties, cucumberArguments, testClassLoader)
    runCucumber(runtime).asInstanceOf[Byte].intValue
  }

  private def runCucumber(runtime: CucumberRuntime) = try { 
    runtime.initialise
    runtime.run
    runtime.printSummary
    runtime.exitStatus
  } catch {
    case e: InvocationTargetException => {
      val cause = if ( e.getCause == null ) e else e.getCause
      error("Error running cucumber. Cause: " + cause.getMessage)
      throw cause
    }
  }

  case class CucumberRuntime(runtime: Runtime, options: RuntimeOptions, loader: AnyRef, 
                             formatter: Formatter, reporter: Reporter, summaryPrinter: SummaryPrinter) {
    private val loaderClass = loader.getClass.getInterfaces()(0)

    def initialise = runtime.writeStepdefsJson()
    def printSummary = summaryPrinter.print(runtime)
    def exitStatus = runtime.exitStatus

    def run = {
      val featureList = (classOf[RuntimeOptions].getMethod("cucumberFeatures", loaderClass)
                                                .invoke(options, loader)
                                                .asInstanceOf[java.util.List[CucumberFeature]]
                                                .asScala)
      featureList foreach { feature => feature.run(formatter, reporter, runtime) }
    }
  }

  private def buildRuntime(properties: Properties, 
                           arguments: Array[String], 
                           classLoader: ClassLoader): CucumberRuntime = try {
    def buildLoader(clazz: Class[_]) = 
      clazz.getConstructor(classOf[ClassLoader]).newInstance(classLoader).asInstanceOf[AnyRef]
  
    val loaderClass = loadCucumberClasses(classLoader)

    val options = new RuntimeOptions(properties, arguments :_*)
    val loader = buildLoader(loaderClass)

    val runtimeConstructor = classOf[Runtime].getConstructor(loaderClass.getInterfaces()(0), classOf[ClassLoader], classOf[RuntimeOptions])
    val runtime = runtimeConstructor.newInstance(loader, classLoader, options).asInstanceOf[Runtime]

    CucumberRuntime(runtime, options, loader, 
                    options.formatter(classLoader), options.reporter(classLoader), new SummaryPrinter(System.out))
  } catch {
    case e => 
      error("Unable to construct cucumber runtime. Please report this as an error. (Details: " + e.getMessage + ")")
      throw e
  }

  private def loadCucumberClasses(classLoader: ClassLoader) = try {
    val multiLoaderClassName = cucumberVersion(classLoader) match {
      case "1.0.9" => MultiLoaderClassName_1_0_9
      case _ => MultiLoaderClassName
    } 

    classLoader.loadClass(multiLoaderClassName)
  } catch {
    case e: ClassNotFoundException =>
      error("Unable to load Cucumber classes. Please check your project dependencies. (Details: " + e.getMessage + ")")
      throw e
  }

  private def cucumberVersion(classLoader: ClassLoader) = {
    val stream = classLoader.getResourceAsStream("cucumber/version.properties")
    try {
      val props = new Properties()
      props.load(stream)
      val version = props.getProperty("cucumber-jvm.version")
      debug("Determined cucumber-jvm version to be: " + version)
      version
    } finally {
      stream.close()
    }
  }
}