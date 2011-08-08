package org.drrandom.tests {

  import org.specs2.mutable._
  import org.drrandom._

    class StandardKernelTests extends Specification {

    "A kernel" should {
      "provide a way to register a type with the container" in {
        val kernel = Kernel()
        kernel += Bind[ITest].To[TestClass]
        val found = kernel.get[ITest]
        found must not be None
      }
      "should provide a way to register an instance with the container" in {
        val kernel = Kernel()
        val test = new TestClass()
        kernel += Bind[ITest].To(test)
        val resolvedInstance = kernel.get[ITest]
        resolvedInstance should not be None

      }
      "should allow for netsted constructor depdendencies" in {
        val kernel = Kernel()
        kernel ++= (Bind[ITest].To[TestClass],Bind[INestedTest].To[TestNested])
        
        var iTest = kernel.get[ITest]
        iTest must not be None

        var result = kernel.get[INestedTest]
        result.get.nested must not beNull
      }
      
      "should throw binding exception if dependent types are not registered" in {
        val kernel = Kernel()
        kernel += Bind[INestedTest].To[TestNested]
 
        kernel.get[INestedTest] must throwA[BindingException]
        
      }
    }
  }
}

  trait ITest {}

  class TestClass extends ITest {}

  trait INestedTest {
    val nested:ITest
  }
  case class TestNested(val nested:ITest) extends INestedTest {}
