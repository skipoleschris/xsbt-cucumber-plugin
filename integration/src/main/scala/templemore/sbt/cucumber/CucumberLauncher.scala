package templemore.sbt.cucumber

import cucumber.runtime.io.ResourceLoader
import cucumber.runtime.scala.ScalaBackend

import _root_.scala.collection.JavaConverters._
import java.lang.reflect.InvocationTargetException
import java.util.Properties

import cucumber.runtime._
import cucumber.runtime.model.CucumberFeature
import gherkin.formatter.Formatter
import gherkin.formatter.Reporter

class CucumberLauncher(debug: (String) => Unit, error: (String) => Unit) {
  
  def apply(cucumberArguments: Array[String],
            testClassLoader: ClassLoader): Int = {
    debug("Cucumber arguments: " + cucumberArguments.mkString(" "))
    val runtime = buildRuntime(System.getProperties, cucumberArguments, testClassLoader)
    runCucumber(runtime).asInstanceOf[Byte].intValue
  }

  private def runCucumber(runtime: CucumberRuntime) = try { 
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

    def printSummary() = runtime.printSummary()
    def exitStatus = runtime.exitStatus

    def run = {
      val featureList = classOf[RuntimeOptions].getMethod("cucumberFeatures", loaderClass)
                                                .invoke(options, loader)
                                                .asInstanceOf[java.util.List[CucumberFeature]]
                                                .asScala
      featureList foreach { feature => feature.run(formatter, reporter, runtime) }
    }
  }

  private def buildRuntime(properties: Properties, 
                           arguments: Array[String], 
                           classLoader: ClassLoader): CucumberRuntime = try {
    def buildLoader(clazz: Class[_]) = 
      clazz.getConstructor(classOf[ClassLoader]).newInstance(classLoader).asInstanceOf[AnyRef]
  
    val loaderClass = loadCucumberClasses(classLoader)

    val options = new RuntimeOptions(new Env(properties), arguments.toList.asJava)
    val loader: ResourceLoader = buildLoader(loaderClass).asInstanceOf[ResourceLoader]

    val scalaBackend = new ScalaBackend(loader)
    val runtimeConstructor = classOf[Runtime].getConstructor(loaderClass.getInterfaces()(0), classOf[ClassLoader], classOf[java.util.Collection[Backend]], classOf[RuntimeOptions])
    val runtime = runtimeConstructor.newInstance(loader, classLoader, Seq(scalaBackend).asJava, options)

    CucumberRuntime(runtime, options, loader, 
                    options.formatter(classLoader), options.reporter(classLoader), new SummaryPrinter(System.out))
  } catch {
    case e: Exception => 
      error("Unable to construct cucumber runtime. Please report this as an error. (Details: " + e.getMessage + ")")
      throw e
  }

  private def loadCucumberClasses(classLoader: ClassLoader) = try {
    classLoader.loadClass("cucumber.runtime.io.MultiLoader")
  } catch {
    case e: ClassNotFoundException =>
      error("Unable to load Cucumber classes. Please check your project dependencies. (Details: " + e.getMessage + ")")
      throw e
  }
}