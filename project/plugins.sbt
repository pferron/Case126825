import sbt.VersionScheme
//Generate JaCoCo reports for test coverage.
addSbtPlugin("com.github.sbt" % "sbt-jacoco" % "3.4.0")

//sbt-scoverage is a plugin for SBT that integrates the scoverage code coverage library.
addSbtPlugin("org.scoverage" % "sbt-scoverage" % "2.0.7")

//SonarQube Integration
addSbtPlugin("com.sonar-scala" % "sbt-sonar" % "2.3.0")

//For generating über jar file
addSbtPlugin("com.eed3si9n" % "sbt-assembly"  % "2.1.1")

ThisBuild / libraryDependencySchemes ++= Seq(
  "org.scala-lang.modules" %% "scala-xml" % VersionScheme.Always
)
