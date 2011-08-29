import sbt._

class ClarityProject(info: ProjectInfo) extends DefaultProject(info) {
	
	val specs2 = "org.specs2" %% "specs2" % "1.5"
	val scalaz = "org.specs2" %% "specs2-scalaz-core" % "6.0.RC2"

	def specs2Framework = new TestFramework("org.specs2.runner.SpecsFramework")
  	override def testFrameworks = super.testFrameworks ++ Seq(specs2Framework)

  	override def includeTest(s: String) = { s.endsWith("Tests") || s.contains("UserGuide") }
}