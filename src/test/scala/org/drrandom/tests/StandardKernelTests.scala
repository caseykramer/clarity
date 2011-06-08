package org.drrandom.tests {

  import org.scalatest.matchers.ShouldMatchers
  import org.scalatest.Spec
  import org.drrandom.{Bind, Kernel}

  class StandardKernelTests extends Spec with ShouldMatchers {

    describe("A kernel") {
      it("should provide a way to register a type with the container") {
        val kernel = Kernel()
        kernel +=(Bind[ITest].To[TestClass])
        val found = kernel.get[ITest]
        found should not be None
      }
      it("should provide a way to register an instance with the container") {
        val kernel = Kernel()
        val test = new TestClass()
        kernel += (Bind[ITest].To(test))
        val resolvedInstance = kernel.get[ITest]
        resolvedInstance should not be None

      }
    }
  }

}

  trait ITest {

  }
  class TestClass extends ITest {

  }
