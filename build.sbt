name := "xsbt-cucumber-plugin"

version := "0.6.2"

organization := "templemore"

scalaVersion := "2.9.2"

sbtPlugin := true

resolvers += "sonatype-snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

publishTo := Some(Resolver.file("publishTo", file((Path.userHome / ".m2" / "repository").toString)))

