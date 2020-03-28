name := "learning-concurrent-programming"

version := "1.0"

scalaVersion := "2.12.2"

libraryDependencies ++= Seq(
	"commons-io" % "commons-io" % "2.6",
	"org.scalatest" %% "scalatest" % "3.2.0-M4" % Test,
	"com.storm-enroute" %% "scalameter" % "0.19"
)

fork := false
