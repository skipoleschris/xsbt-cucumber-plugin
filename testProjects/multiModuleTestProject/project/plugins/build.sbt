resolvers += Resolver.file("Local Repo", file((Path.userHome / ".m2" / "repository").toString))

libraryDependencies += "templemore" %% "xsbt-cucumber-plugin" % "0.1"
