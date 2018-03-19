import _root_.bintray.Bintray
import scalariform.formatter.preferences._

lazy val Versions = new {
  val akka = "2.5.7"
  val akkaHttp = "10.1.0"
  val scopt = "3.7.0"
  val scalaTest = "3.0.4"
  val scalaVersion = "2.12.4"
}

lazy val Libraries = new {
  val akka            = "com.typesafe.akka" %% "akka-actor"       % Versions.akka
  val akkaStream      = "com.typesafe.akka" %% "akka-stream"      % Versions.akka
  val akkaHttp        = "com.typesafe.akka" %% "akka-http"        % Versions.akkaHttp
  val scopt           = "com.github.scopt"  %% "scopt"            % Versions.scopt
  val akkaTestKit     = "com.typesafe.akka" %% "akka-testkit"     % Versions.akka        % "test"
  val scalaTest       = "org.scalatest"     %% "scalatest"        % Versions.scalaTest   % "test"
}

lazy val releaseSettings = Seq(
  bintrayRepository := "universal",
  bintrayPackage := (name in LocalRootProject).value,
  publishTo := Some(Resolver.bintrayRepo("fsat", bintrayRepository.value))
)

scalaVersion in ThisBuild := Versions.scalaVersion

organization in ThisBuild := "au.id.fsat"
organizationName in ThisBuild := "Felix Satyaputra"
startYear in ThisBuild := Some(2018)
licenses in ThisBuild += ("Apache-2.0", new URL("https://www.apache.org/licenses/LICENSE-2.0.txt"))

scalariformPreferences in ThisBuild := scalariformPreferences.value
    .setPreference(AlignSingleLineCaseStatements, true)
    .setPreference(AllowParamGroupsOnNewlines, true)

lazy val `release-example` = project
  .in(file("."))
  .enablePlugins(AutomateHeaderPlugin)
  .aggregate(
    example
  )
  .settings(releaseSettings)
  .settings(
    skip in publish := true,
    bintrayRelease := ((): Unit),

    // Skip CI build when release commit is pushed to prevent recursive build from occurring
    releaseCommitMessage := "${releaseCommitMessage.value} [ci skip]"
  )

lazy val example = project
  .in(file("example"))
  .enablePlugins(AutomateHeaderPlugin)
  .settings(releaseSettings)
  .settings(
    libraryDependencies ++= Seq(
      Libraries.akka,
      Libraries.akkaStream,
      Libraries.akkaHttp,
      Libraries.scopt,
      Libraries.scalaTest,
      Libraries.akkaTestKit
    ),
    mainClass in assembly := Some("au.id.fsat.examples.Main"),
    assemblyJarName in assembly := s"example-${(version in ThisBuild).value}-all.jar",
    publish := {
      val log = streams.value.log

      val bintrayCredentials = bintrayEnsureCredentials.value
      val bintrayRepo = Bintray.cachedRepo(bintrayCredentials, bintrayOrganization.value, bintrayRepository.value)
      val packageName = bintrayPackage.value
      val fatJar = assembly.value

      log.info(s"Uploading [$packageName/${fatJar.getName}] to [${bintrayCredentials.user}/${bintrayRepository.value}]")
      bintrayRepo.upload(packageName, version.value, fatJar.getName, fatJar, log)

      log.info(s"Releasing [$packageName/${fatJar.getName}] version [${version.value}]")
      bintrayRepo.release(packageName, version.value, log)
    }
  )
