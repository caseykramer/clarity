package org.drrandom
import collection.mutable.HashMap
import scala.None
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

class BindingException(message:String) extends Exception(message)

class InstanceBindingInfo[T,U](override val source:Manifest[T], dest:Manifest[U],instance:U) extends BindingInfo[T,U](source,dest) {
  override def create:Any = instance
}

trait TypeBinder[T] {
  val sourceType:Manifest[T]

  def To[U<:T](implicit manifest:Manifest[U]) = {
    new BindingInfo[T,U](this.sourceType,manifest)
  }

  def To[U<:T:Manifest](instance:U) = {
    val dest = manifest[U]
    new InstanceBindingInfo[T,U](sourceType,dest,instance)
  }
}

abstract class Kernel {
  def +=(binding:BindingInfo[_,_])
  def register(binding:BindingInfo[_,_])
  def ++=(binding:BindingInfo[_,_]*)
  def register(binding:BindingInfo[_,_]*)
  def get[T](implicit man:Manifest[T]):Option[T]
  def get(clazz:java.lang.Class[_]):Option[Any]
}

protected class StandardKernel extends Kernel {
  
  private var _bindings = HashMap[Manifest[_],BindingInfo[_,_]]()

  def +=(binding:BindingInfo[_,_]) = {
    binding.kernel = this
    _bindings += binding.source -> binding
  }

  def register(binding:BindingInfo[_,_]) = {
    binding.kernel = this
    _bindings += binding.source -> binding
  }

  def ++=(binding:BindingInfo[_,_]*) = {
    binding.foreach(_.kernel = this)
    _bindings ++= binding.map(b => b.source -> b)
  }

  def register(binding:BindingInfo[_,_]*) = {
    binding.foreach(_.kernel = this)
    _bindings ++= binding.map(b => b.source -> b)
  }

  def get[T](implicit man:Manifest[T]):Option[T] = {
    _bindings.get(man) match {
      case None => None
      case Some(b:BindingInfo[T,_]) =>Some(b.create.asInstanceOf[T])
    }
  }

  def get(clazz:java.lang.Class[_]):Option[Any] = {
    val keys = _bindings.keys.filter(_.erasure == clazz)
    if (keys.size == 0)
      return None

    _bindings.get(keys.head) match {
      case None => None
      case Some(b:BindingInfo[_,_]) => Some(b.create)
    }
  }
}

object Kernel {
  def apply():Kernel = new StandardKernel()
}

class Bind[T](implicit val sourceType:Manifest[T]) extends TypeBinder[T] {

}

object Bind {
  def apply[T](implicit man:Manifest[T]) = new Bind[T]()
}