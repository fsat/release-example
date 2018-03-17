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

scalaVersion in ThisBuild := Versions.scalaVersion

organization in ThisBuild := "au.id.fsat"
organizationName in ThisBuild := "Felix Satyaputra"
startYear in ThisBuild := Some(2018)
licenses in ThisBuild += ("Apache-2.0", new URL("https://www.apache.org/licenses/LICENSE-2.0.txt"))

scalariformPreferences in ThisBuild := scalariformPreferences.value
    .setPreference(AlignSingleLineCaseStatements, true)
    .setPreference(AllowParamGroupsOnNewlines, true)

enablePlugins(AutomateHeaderPlugin)

lazy val `release-example` = project
  .in(file("."))
  .aggregate(
    example
  )

lazy val example = project
  .in(file("example"))
  .settings(Seq(
    libraryDependencies ++= Seq(
      Libraries.akka,
      Libraries.akkaStream,
      Libraries.akkaHttp,
      Libraries.scopt,
      Libraries.scalaTest,
      Libraries.akkaTestKit
    )
  ))