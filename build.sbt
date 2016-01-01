name := "learning-concurrent-programming"

version := "1.0"

scalaVersion := "2.11.6"

resolvers ++= Seq(
	"Sonatype OSS Snapshots" at
	"https://oss.sonatype.org/content/repositories/snapshots",
	"Sonatype OSS Releases" at
	"https://oss.sonatype.org/content/repositories/releases",
	"Typesafe Repository" at
	"http://repo.typesafe.com/typesafe/releases/"
)

libraryDependencies ++= Seq(
	"commons-io" % "commons-io" % "2.4",
  "org.scalatest" % "scalatest_2.11" % "2.2.4" % "test"
)

/* Many of the examples start a concurrent computation that continues 
executing after the main execution stops. This guarantees that the examples
run in the same JVM as the SBT process */
fork := false
