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

      "should allow binding of open generic types, with final type parameters defined at time of request" in {
        var kernel = Kernel()
        kernel += Bind[IGeneric[_]].To[OpenGeneric[_]]

        val result = kernel.get[IGeneric[String]]

        result must beSome
        result.get must beAnInstanceOf[IGeneric[String]]
      }

      "should resolve matching generic types in preference to any open generic types" in {
        val kernel = Kernel()
        kernel += Bind[IGeneric[_]].To[OpenGeneric[_]]
        kernel += Bind[IGeneric[String]].To[StringGeneric]

        val stringResult = kernel.get[IGeneric[String]]

        stringResult must beSome
        stringResult.get must beAnInstanceOf[IGeneric[String]]

        val intResult = kernel.get[IGeneric[List[Int]]]
        
        intResult must beSome
        intResult.get must beAnInstanceOf[IGeneric[List[Int]]]
      }
    }
  }
}

  trait IGeneric[T]

  class OpenGeneric[T]() extends IGeneric[T] {
    
  }

  class StringGeneric() extends IGeneric[String] {
    
  }

  trait ITest {}

  class TestClass extends ITest {}

  trait INestedTest {
    val nested:ITest
  }
  case class TestNested(val nested:ITest) extends INestedTest {}
