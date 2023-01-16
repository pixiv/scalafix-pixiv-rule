import com.typesafe.sbt.SbtLicenseReport.autoImportImpl.licenseReportDir

lazy val V = _root_.scalafix.sbt.BuildInfo

lazy val rulesCrossVersions = Seq(V.scala213, V.scala212)
lazy val scala3Version = "3.0.1"

ThisBuild / scalafixScalaBinaryVersion := CrossVersion.binaryScalaVersion(scalaVersion.value)
ThisBuild / scalafixDependencies += "com.github.liancheng" %% "organize-imports" % "0.6.0"
ThisBuild / scalafixDependencies += "com.sandinh" %% "scala-rewrites" % "1.1.0-M1"

inThisBuild(
  List(
    organization := "net.pixiv",
    homepage := Some(url("https://github.com/pixiv/scalafix-pixiv-rule")),
    licenses := List(
      "Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")
    ),
    developers := List(
      Developer(
        "Javakky-pxv",
        "Kazuma Iemura",
        "javakky@pixiv.co.jp",
        url("https://github.com/Javakky-pxv")
      )
    ),
    semanticdbEnabled := true,
    semanticdbVersion := scalafixSemanticdb.revision,
    scalaVersion := "2.13.7",
    sonatypeCredentialHost := "s01.oss.sonatype.org",
    sonatypeRepository := "https://s01.oss.sonatype.org/service/local"
  )
)

lazy val `scalafix-rule-pixiv` = (project in file("."))
  .aggregate(
    rules.projectRefs ++
      input.projectRefs ++
      output.projectRefs ++
      tests.projectRefs: _*
  )
  .settings(
    publish / skip := true
  )

lazy val src = (project in file("rules"))
  .settings(
    libraryDependencies ++= Seq(
      "ch.epfl.scala" %% "scalafix-core" % V.scalafixVersion,
      "org.scalatest" %% "scalatest" % "3.2.11" % "test"
    ),
    scalacOptions ++= Seq(
      "-deprecation",
      "-feature",
      "-Ywarn-unused:imports,locals,patvars"
    ),
    semanticdbEnabled := true,
    semanticdbVersion := scalafixSemanticdb.revision,
    publish / skip := true,
    licenseReportTitle := "NOTICE",
    licenseReportDir := `scalafix-rule-pixiv`.base,
    licenseReportTypes := Seq(MarkDown)
  )

lazy val rules = projectMatrix
  .settings(
    moduleName := "scalafix-pixiv-rule",
    libraryDependencies ++= Seq(
      "ch.epfl.scala" %% "scalafix-core" % V.scalafixVersion,
      "org.scalatest" %% "scalatest" % "3.2.11" % "test"
    )
  )
  .defaultAxes(VirtualAxis.jvm)
  .jvmPlatform(rulesCrossVersions)

lazy val input = projectMatrix
  .settings(
    publish / skip := true,
    libraryDependencies ++= Seq(
      "org.mockito" % "mockito-core" % "4.8.1",
      "org.scalatest" %% "scalatest" % "3.2.11"
    )
  )
  .defaultAxes(VirtualAxis.jvm)
  .jvmPlatform(scalaVersions = rulesCrossVersions :+ scala3Version)

lazy val output = projectMatrix
  .settings(
    publish / skip := true,
    libraryDependencies ++= Seq(
      "org.mockito" % "mockito-core" % "4.8.1",
      "org.scalatest" %% "scalatest" % "3.2.11"
    )
  )
  .defaultAxes(VirtualAxis.jvm)
  .jvmPlatform(scalaVersions = rulesCrossVersions :+ scala3Version)

lazy val testsAggregate = Project("tests", file("target/testsAggregate"))
  .aggregate(tests.projectRefs: _*)

lazy val tests = projectMatrix
  .settings(
    publish / skip := true,
    libraryDependencies += "ch.epfl.scala" % "scalafix-testkit" % V.scalafixVersion % Test cross CrossVersion.full,
    scalafixTestkitOutputSourceDirectories :=
      TargetAxis
        .resolve(output, Compile / unmanagedSourceDirectories)
        .value,
    scalafixTestkitInputSourceDirectories :=
      TargetAxis
        .resolve(input, Compile / unmanagedSourceDirectories)
        .value,
    scalafixTestkitInputClasspath :=
      TargetAxis.resolve(input, Compile / fullClasspath).value,
    scalafixTestkitInputScalacOptions :=
      TargetAxis.resolve(input, Compile / scalacOptions).value,
    scalafixTestkitInputScalaVersion :=
      TargetAxis.resolve(input, Compile / scalaVersion).value
  )
  .defaultAxes(
    rulesCrossVersions.map(VirtualAxis.scalaABIVersion) :+ VirtualAxis.jvm: _*
  )
  .customRow(
    scalaVersions = Seq(V.scala213),
    axisValues = Seq(TargetAxis(V.scala213), VirtualAxis.jvm),
    settings = Seq()
  )
  .customRow(
    scalaVersions = Seq(V.scala212),
    axisValues = Seq(TargetAxis(V.scala212), VirtualAxis.jvm),
    settings = Seq()
  )
  .dependsOn(rules)
  .enablePlugins(ScalafixTestkitPlugin)
