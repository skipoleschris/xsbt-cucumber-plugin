package templemore.xsbt.cucumber

/**
 * @author Chris Turner
 */
sealed trait CucumberMode
object Normal extends CucumberMode { override def toString = "Normal Console Output" }
object Developer extends CucumberMode { override def toString = "Developer Console Ouptut" }
object HtmlReport extends CucumberMode { override def toString = "Html Report Output" }

