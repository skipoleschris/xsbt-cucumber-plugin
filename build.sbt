name := "cucumber-sbt-plugin"

version := "0.6"

organization := "templemore"

scalaVersion := "2.8.1"

sbtPlugin := true

publishTo := Some(Resolver.file("Local Repo", file((Path.userHome / ".m2" / "repository").toString)))
