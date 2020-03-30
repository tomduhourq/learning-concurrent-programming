name := "learning-concurrent-programming"

version := "1.0"

scalaVersion := "2.12.2"

libraryDependencies ++= Seq(
	"commons-io" % "commons-io" % "2.6",
	"org.scalactic" %% "scalactic" % "3.1.1",
	"org.scalatest" %% "scalatest" % "3.1.1" % Test,
	"com.storm-enroute" %% "scalameter" % "0.19"
)

fork := false
