package templemore.sbt.cucumber

import scala.collection.JavaConverters._
import java.lang.reflect.InvocationTargetException
import java.util.Properties
import java.io.PrintStream

class ReflectingCucumberLauncher(debug: (String) => Unit, error: (String) => Unit) {
  
  private val RuntimeOptionsClassName = "cucumber.runtime.RuntimeOptions"
  private val MultiLoaderClassName = "cucumber.runtime.io.MultiLoader"
  private val MultiLoaderClassName_1_0_9 = "cucumber.io.MultiLoader"
  private val RuntimeClassName = "cucumber.runtime.Runtime"
  private val FormatterClassName = "gherkin.formatter.Formatter"
  private val ReporterClassName = "gherkin.formatter.Reporter"
  private val SummaryPrinterClassName = "cucumber.runtime.snippets.SummaryPrinter"

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

  case class CucumberRuntime(runtime: AnyRef, options: AnyRef, loader: AnyRef, 
                             formatter: AnyRef, reporter: AnyRef, summaryPrinter: AnyRef,
                             classLoader: ClassLoader, formatterClass: Class[_], reporterClass: Class[_]) {
    private val runtimeClass = runtime.getClass
    private val optionsClass = options.getClass
    private val loaderClass = loader.getClass.getInterfaces()(0)
    private val summaryPrinterClass = summaryPrinter.getClass

    def initialise = runtimeClass.getMethod("writeStepdefsJson").invoke(runtime)
    def printSummary = summaryPrinterClass.getMethod("print", runtimeClass).invoke(summaryPrinter, runtime)
    def exitStatus = runtimeClass.getMethod("exitStatus").invoke(runtime)

    def run = {
      val featureList = optionsClass.getMethod("cucumberFeatures", loaderClass).invoke(options, loader).asInstanceOf[java.util.List[Object]].asScala
      featureList foreach { feature =>
        val featureClass = feature.getClass
        featureClass.getMethod("run", formatterClass, reporterClass, runtimeClass).invoke(feature, formatter, reporter, runtime)
      }
    }
  }

  private def buildRuntime(properties: Properties, 
                           arguments: Array[String], 
                           classLoader: ClassLoader): CucumberRuntime = try {
    def buildLoader(clazz: Class[_]) = 
      clazz.getConstructor(classOf[ClassLoader]).newInstance(classLoader).asInstanceOf[AnyRef]
    def buildOptions(clazz: Class[_]) = 
      clazz.getConstructor(classOf[Properties], classOf[Array[String]]).newInstance(properties.asInstanceOf[AnyRef], arguments).asInstanceOf[AnyRef]
  
    val (runtimeClass, optionsClass, loaderClass, formatterClass, reporterClass, summaryPrinterClass) = loadCucumberClasses(classLoader)

    val options = buildOptions(optionsClass)
    val loader = buildLoader(loaderClass)

    val formatter = optionsClass.getMethod("formatter", classOf[ClassLoader]).invoke(options, classLoader).asInstanceOf[AnyRef]
    val reporter = optionsClass.getMethod("reporter", classOf[ClassLoader]).invoke(options, classLoader).asInstanceOf[AnyRef]

    val summaryPrinterConstructor = summaryPrinterClass.getConstructor(classOf[PrintStream])
    val summaryPrinter = summaryPrinterConstructor.newInstance(System.out).asInstanceOf[AnyRef]

    val runtimeConstructor = runtimeClass.getConstructor(loaderClass.getInterfaces()(0), classOf[ClassLoader], optionsClass)
    val runtime = runtimeConstructor.newInstance(loader, classLoader, options).asInstanceOf[AnyRef]

    CucumberRuntime(runtime, options, loader, formatter, reporter, summaryPrinter, classLoader, formatterClass, reporterClass)
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

    val runtimeOptionsClass = classLoader.loadClass(RuntimeOptionsClassName)
    val multiLoaderClass = classLoader.loadClass(multiLoaderClassName)
    val runtimeClass = classLoader.loadClass(RuntimeClassName)
    val formatterClass = classLoader.loadClass(FormatterClassName)
    val reporterClass = classLoader.loadClass(ReporterClassName)
    val summaryPrinterClass = classLoader.loadClass(SummaryPrinterClassName)
    (runtimeClass, runtimeOptionsClass, multiLoaderClass, formatterClass, reporterClass, summaryPrinterClass)
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