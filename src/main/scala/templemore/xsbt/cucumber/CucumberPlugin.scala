package templemore.xsbt.cucumber

import sbt._
import Keys._
import Project.Initialize
import templemore.jruby.JRubyDependencies

/**
 * @author Chris Turner
 */
object CucumberPlugin extends Plugin with JRubyDependencies {

  val cucumber = InputKey[Unit]("cucumber")

  def cucumberInputTask(argTask: TaskKey[Seq[String]]) =
    argTask map { (args: Seq[String]) =>
      println("Cucumber input task")
      args foreach println
    }


  val cucumberSettings = Seq(
    resolvers ++= jRubyResolvers,
    libraryDependencies ++= jRubyDependencies,

    cucumber <<= inputTask(cucumberInputTask)
  )
}