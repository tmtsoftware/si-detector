/* This is the placeholder for setting JVM options via sbt native packager.
You can add or update below JVM options as per application needs.

javaOptions in Universal ++= Seq(
//   -J params will be added as jvm parameters
  "-J-Xmx2GB",
  "-J-Xms2GB",
//   gc tuning - Enable and change these parameters as per your need
   "J-XX:+UnlockExperimentalVMOptions",
   "J-XX:+UnlockDiagnosticVMOptions",
   "J-XX:+UseG1GC", // this option is enabled by default on jdk9+
   "J-XX:MaxGCPauseMillis=50", // Sets a target for the maximum GC pause time. This is a soft goal, and the JVM will make its best effort to achieve it
   "J-Xlog:gc*:file=gc-%%t.log",
   "J-XX:+UseStringDeduplication",
)
*/

lazy val aggregatedProjects: Seq[ProjectReference] = Seq(
  `sidetector-assembly`,
  `sidetector-hcd`,
  `sidetector-deploy`
)

lazy val `sidetector` = project
  .in(file("."))
  .aggregate(aggregatedProjects: _*)

lazy val `sidetector-assembly` = project
  .settings(
    libraryDependencies ++= Dependencies.SidetectorAssembly
  )

lazy val `sidetector-hcd` = project
  .settings(
    libraryDependencies ++= Dependencies.SidetectorHcd
  )

lazy val `sidetector-deploy` = project
  .dependsOn(
    `sidetector-assembly`,
    `sidetector-hcd`
  )
  .enablePlugins(JavaAppPackaging, CswBuildInfo)
  .settings(
    libraryDependencies ++= Dependencies.SidetectorDeploy
  )
