package org.drrandom.tests {

  import org.drrandom.tests.testClasses._
  import org.scalatest.matchers.ShouldMatchers
  import org.scalatest.Spec
  import org.drrandom.{Bind, Kernel}

  class StandardKernelTests extends Spec with ShouldMatchers {

    describe("A kernel") {
      it("should provide a way to register a type with the container") {
        val kernel = Kernel()
        kernel +=(Bind[ITest].To[Test])
        val found = kernel.get[ITest]
        found should not be None
      }
    }
  }

}

package org.drrandom.tests.testClasses {
  trait ITest
  class Test extends ITest
}
