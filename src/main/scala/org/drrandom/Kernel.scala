package org.drrandom
import collection.mutable.HashMap
import reflect._
import scala.None

class BindingInfo[T,U](val source:Manifest[T],dest:Manifest[U]) {
  def erasedType = dest.erasure
  def create:Any = erasedType.newInstance()
}

trait TypeBinder[T] {
  val sourceType:Manifest[T]

  def To[U<:T](implicit manifest:Manifest[U]) = {
    new BindingInfo(sourceType,manifest)
  }
}

abstract class Kernel {
  def +=(binding:BindingInfo[_,_]):Unit
  def register(binding:BindingInfo[_,_]):Unit
  def ++=(binding:Iterable[BindingInfo[_,_]]):Unit
  def register(binding:Iterable[BindingInfo[_,_]]):Unit
  def get[T](implicit man:Manifest[T]):Option[T]
}

protected class StandardKernel extends Kernel {
  class BasicTypeBinder[T](implicit val sourceType:Manifest[T]) extends TypeBinder[T] {

  }

  private var _bindings = HashMap[Manifest[_],BindingInfo[_,_]]()

  def +=(binding:BindingInfo[_,_]) = {
    _bindings += binding.source -> binding
  }

  def register(binding:BindingInfo[_,_]) = {
    _bindings += binding.source -> binding
  }

  def ++=(binding:Iterable[BindingInfo[_,_]]) = {
    _bindings ++= binding.map(b => b.source -> b)
  }

  def register(binding:Iterable[BindingInfo[_,_]]) = {
    _bindings ++= binding.map(b => b.source -> b)
  }

  def get[T](implicit man:Manifest[T]):Option[T] = {
    _bindings.get(man) match {
      case None => None
      case Some(b:BindingInfo[T,_]) =>Some(b.create.asInstanceOf[T])
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