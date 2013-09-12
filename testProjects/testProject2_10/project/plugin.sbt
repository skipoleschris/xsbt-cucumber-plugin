resolvers += "Templemore Repository" at "http://templemore.co.uk/repo"

resolvers += Resolver.file("local-ivy-repo", file(Path.userHome + "/.ivy2/local"))(Resolver.ivyStylePatterns)

addSbtPlugin("templemore" % "sbt-cucumber-plugin" % "0.7.3-SNAPSHOT")
