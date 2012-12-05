resolvers += Resolver.file("Local Repo", file((Path.userHome / ".m2" / "repository").toString))

addSbtPlugin("templemore" % "sbt-cucumber-plugin" % "0.7.1")
