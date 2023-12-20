import com.typesafe.config._

val conf = ConfigFactory.parseFile(new File("build.conf"))

name := "criteo_category_advertiser_dly"

val envVersion = sys.env.getOrElse("build_version", "0.1")
lazy val scala212 = conf.getString("scala.version")
lazy val buildVersion = envVersion

ThisBuild / organization := "com.tgt.dse.mdf.criteoad.pipeline"
ThisBuild / version := envVersion
ThisBuild / scalaVersion := scala212

lazy val spark3 = conf.getString("spark.version")
lazy val scalaLoggingVersion = conf.getString("scala.logging.version")
lazy val mdfCommonVersion = conf.getString("mdf.common.version")
val publishMavenStyle = true
lazy val sonar_token = sys.env.getOrElse("SONAR_TOKEN", "")

lazy val core = (project in file("."))
  .settings(
    /**
     * Repositories where the dependencies will be pulled from.
     */
    resolvers := Seq(
      "BigRed Artifactory Repo"         at "https://binrepo.target.com/artifactory/bigRED",
      "repo1-cache Artifactory Repo"    at "https://binrepo.target.com/artifactory/repo1-cache",
      "jcenter-cache Artifactory Repo"  at "https://binrepo.target.com/artifactory/jcenter-cache",
      "maven-central-cache Artifactory Repo" at "https://binrepo.target.com/artifactory/maven-central-cache",
      "Marketing Common Artifactory"    at "https://binrepo.target.com/artifactory/mdf",
      "Kelsa Artifactory" at "https://binrepo.target.com/artifactory/kelsa"
    ),

    Test / fork := true,
    //Java options for test
    javaOptions ++= Seq("-Xms512M","-Xmx2G","-XX:+CMSClassUnloadingEnabled"),

    assembly / assemblyMergeStrategy := {
      case PathList("META-INF", _*) => MergeStrategy.discard
      case "log4j.properties" => MergeStrategy.discard
      case x => MergeStrategy.first
    },

    Test / parallelExecution := true,

    /**
     * coverage stuff
     */
    coverageHighlighting := true,

    //Scoverage settings
    coverageMinimumStmtTotal := 70,
    coverageFailOnMinimum := true,

    // jacoco settings
    jacocoReportSettings := JacocoReportSettings(
      title = name.value,
      formats = Seq(
        JacocoReportFormats.ScalaHTML,
        JacocoReportFormats.CSV,
        JacocoReportFormats.XML
      )).withThresholds(
      JacocoThresholds(
        line = 70
      )),

    libraryDependencies ++= Seq(
      "org.scalamock" %% "scalamock" % conf.getString("scalamock.version") % Test,
      "org.slf4j" % "slf4j-api" % conf.getString("slf4j.version"),
      "org.slf4j" % "slf4j-reload4j" % conf.getString("slf4j.version"),
      "org.scalatest" %% "scalatest" % conf.getString("scalatest.version") % Test,
      "org.apache.spark" %% "spark-core" % spark3 % Provided,
      "org.apache.spark" %% "spark-sql" % spark3 % Provided,
      "org.scala-lang" % "scala-library" % scala212,
      "com.tgt.dsc.kelsa.datapipeline" %% "kelsa-core" % conf.getString("kelsa.version"),
      "com.tgt.dse.mdf.common.pipeline" %% "mdf_dpp_common" % mdfCommonVersion,
      "org.apache.httpcomponents" % "httpclient" % conf.getString("httpclient.version") % Provided
    ),
    coverageScalacPluginVersion := conf.getString("scalac.plugin.version"),
    excludeDependencies ++= Seq(ExclusionRule("log4j", "log4j")),
    sonarProperties ++= Map(
      "sonar.scala.version" -> "2.12",
      "sonar.scala.scoverage.reportPath" -> "target/scala-2.12/scoverage-report/scoverage.xml",
      "sonar.scala.scapegoat.reportPath" -> "target/scala-2.12/scapegoat-report/scapegoat.xml",
      "sonar.coverage.jacoco.xmlReportPaths" -> "target/scala-2.12/jacoco/report/jacoco.xml",
      "sonar.host.url" -> "https://desonar.prod.target.com",
      "sonar.projectKey" -> "data-engineering-mktg-data-foundation.criteoad_dp_pipeline",
      "sonar.projectName" -> "data-engineering-mktg-data-foundation.criteoad_dp_pipeline",
      "sonar.verbose" -> "true",
      "sonar.login" -> sonar_token
    ),
    assemblyJarName := s"${name.value}_2.12-$envVersion.jar"
  )
