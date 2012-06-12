resolvers += Resolver.file("Local Repo", file((Path.userHome / ".m2" / "repository").toString))

addSbtPlugin("templemore" % "xsbt-cucumber-plugin" % "0.5.0")
