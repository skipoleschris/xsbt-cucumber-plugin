name := "xsbt-cucumber-plugin"

version := "0.1"

organization := "templemore"

scalaVersion := "2.8.1"

sbtPlugin := true

publishTo := Some(Resolver.file("Local Repo", file((Path.userHome / ".m2" / "repository").toString)))
