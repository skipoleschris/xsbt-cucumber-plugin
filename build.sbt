name := "xsbt-cucumber-plugin"

version := "0.5.0"

organization := "templemore"

scalaVersion := "2.9.1"

sbtPlugin := true

resolvers += "sonatype-snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

publishTo := Some(Resolver.file("Local Repo", file((Path.userHome / ".m2" / "repository").toString)))

