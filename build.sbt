name := "search-aggregator"

version := "0.1"

scalaVersion := "2.12.6"


lazy val akkaHttpVersion = "10.1.5"
lazy val akkaVersion    = "2.5.16"

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization    := "$organization$",
      scalaVersion    := "$scala_version$"
    )),
    name := "$name$",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-http"            % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-http-xml"        % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-stream"          % akkaVersion,

      "com.typesafe.akka" %% "akka-http-testkit"    % akkaHttpVersion % Test,
      "com.typesafe.akka" %% "akka-testkit"         % akkaVersion     % Test,
      "com.typesafe.akka" %% "akka-stream-testkit"  % akkaVersion     % Test,
      "org.scalatest"     %% "scalatest"            % "3.0.5"         % Test,

      "com.typesafe.akka"          %% "akka-persistence" % akkaVersion,
      "org.iq80.leveldb"            % "leveldb"          % "0.7",
      "org.fusesource.leveldbjni"   % "leveldbjni-all"   % "1.8"
    )
  )