
name := "Diary"

version := "1.0"

scalaVersion := "2.12.4"

resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"
resolvers += "lightshed-maven" at "http://dl.bintray.com/content/lightshed/maven"

//
// variables
//

lazy val catsVersion = "1.0.1"
lazy val circeVersion = "0.9.0"
lazy val doobieVersion = "0.5.0-M13"
lazy val finchVersion = "0.16.1"
lazy val pureconfigVersion = "0.9.0"
lazy val refinedVersion = "0.8.6"
lazy val twitterUtilVersion = "17.12.0"

libraryDependencies ++= Seq(
  "org.typelevel"         %% "cats-core"            % catsVersion,
  "io.circe"              %% "circe-core"           % circeVersion,
  "io.circe"              %% "circe-generic"        % circeVersion,
  "io.circe"              %% "circe-generic-extras" % circeVersion,
  "io.circe"              %% "circe-parser"         % circeVersion,
  "io.circe"              %% "circe-java8"          % circeVersion,
  "io.circe"              %% "circe-refined"        % circeVersion,
  "com.github.pureconfig" %% "pureconfig"           % pureconfigVersion,
  "com.github.pureconfig" %% "pureconfig-cats"      % pureconfigVersion,
  "eu.timepit"            %% "refined"              % refinedVersion,
  "eu.timepit"            %% "refined-pureconfig"   % refinedVersion,
  "com.twitter"           %% "util-app"             % twitterUtilVersion,
  "com.twitter"           %% "finagle-redis"        % twitterUtilVersion,
  "io.catbird"            %% "catbird-util"         % twitterUtilVersion,
  "org.tpolecat"          %% "doobie-core"          % doobieVersion,
  "org.tpolecat"          %% "doobie-hikari"        % doobieVersion,
  "com.github.finagle"    %% "finch-circe"          % finchVersion,
  "ch.qos.logback"        % "logback-classic"       % "1.2.3"
)

assemblyMergeStrategy in assembly := {
  case PathList(ps @ _*) if ps.last endsWith ".properties" => MergeStrategy.first
  case x =>
    val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(x)
}
