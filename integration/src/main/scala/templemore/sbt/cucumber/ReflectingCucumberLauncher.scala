package templemore.sbt.cucumber

import java.lang.reflect.InvocationTargetException
import java.util.Properties

class ReflectingCucumberLauncher(debug: (String) => Unit, error: (String) => Unit) {
  
  private val RuntimeOptionsClassName = "cucumber.runtime.RuntimeOptions"
  private val MultiLoaderClassName = "cucumber.runtime.io.MultiLoader"
  private val MultiLoaderClassName_1_0_9 = "cucumber.io.MultiLoader"
  private val RuntimeClassName = "cucumber.runtime.Runtime"

  def apply(cucumberArguments: Array[String],
            testClassLoader: ClassLoader): Int = {
    debug("Cucumber arguments: " + cucumberArguments.mkString(" "))
    val runtime = buildRuntime(System.getProperties, cucumberArguments, testClassLoader)
    runCucumber(runtime).asInstanceOf[Byte].intValue
  }

  private def runCucumber(runtime: AnyRef) = try { 
    val runtimeClass = runtime.getClass
    runtimeClass.getMethod("writeStepdefsJson").invoke(runtime)
    runtimeClass.getMethod("run").invoke(runtime)
    runtimeClass.getMethod("exitStatus").invoke(runtime)
  } catch {
    case e: InvocationTargetException => {
      val cause = if ( e.getCause == null ) e else e.getCause
      error("Error running cucumber. Cause: " + cause.getMessage)
      throw cause
    }
  }

  private def buildRuntime(properties: Properties, 
                           arguments: Array[String], 
                           classLoader: ClassLoader): AnyRef = {
    def buildLoader(clazz: Class[_]) = 
      clazz.getConstructor(classOf[ClassLoader]).newInstance(classLoader).asInstanceOf[AnyRef]
    def buildOptions(clazz: Class[_]) = 
      clazz.getConstructor(classOf[Properties], classOf[Array[String]]).newInstance(properties.asInstanceOf[AnyRef], arguments).asInstanceOf[AnyRef]
  
    val (runtimeClass, optionsClass, loaderClass) = loadCucumberClasses(classLoader)
    val runtimeConstructor = runtimeClass.getConstructor(loaderClass.getInterfaces()(0), classOf[ClassLoader], optionsClass)
    runtimeConstructor.newInstance(buildLoader(loaderClass), classLoader, buildOptions(optionsClass)).asInstanceOf[AnyRef]
  }

  private def loadCucumberClasses(classLoader: ClassLoader) = try {
    val multiLoaderClassName = cucumberVersion(classLoader) match {
      case "1.0.9" => MultiLoaderClassName_1_0_9
      case _ => MultiLoaderClassName
    } 

    val runtimeOptionsClass = classLoader.loadClass(RuntimeOptionsClassName)
    val multiLoaderClass = classLoader.loadClass(multiLoaderClassName)
    val runtimeClass = classLoader.loadClass(RuntimeClassName)
    (runtimeClass, runtimeOptionsClass, multiLoaderClass)
  } catch {
    case e: ClassNotFoundException =>
      error("Unable to load Cucumber classes. Please check your project dependencied. (Details: " + e.getMessage + ")")
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