package org.drrandom

import java.lang.Class
import java.lang.reflect._
import reflect.{Manifest}

class BindingInfo[T,U](val source:Manifest[T],dest:Manifest[U]) {

  val creator: () => Any = createBuilder
  def erasedType = dest.erasure
  def create:Any = creator()
  var kernel:Kernel = null

  private def createBuilder:() => Any = {
    val bestCtor = findBestConstructor
    val params = bestCtor.getParameterTypes
    if(params.size == 0)
      return () => bestCtor.newInstance()
    else
      return () => {
        val args = params.map(p => kernel.get(p) match {
          case Some(i) => i.asInstanceOf[java.lang.Object]
          case None => throw new BindingException("Unable to find dependent type: "+p.toString+" while creating instance of "+erasedType.toString)
        }).toArray
        bestCtor.newInstance(args:_*)
      }
  }

  def findBestConstructor = dest.erasure.getConstructors.min(Ordering[Int].on[Constructor[_]](_.getParameterTypes.size))
}

class InstanceBindingInfo[T,U](override val source:Manifest[T], dest:Manifest[U],instance:U) extends BindingInfo[T,U](source,dest) {
  override def create:Any = instance
}
