package org.drrandom

import collection.mutable.HashMap
import scala.None
import java.lang.Class
import java.lang.reflect._
import reflect.{Manifest}



trait Kernel {
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
    register(binding)
  }

  def register(binding:BindingInfo[_,_]) = {
    binding.kernel = this
    _bindings += binding.source -> binding
  }

  def ++=(bindings:BindingInfo[_,_]*) = {
    register(bindings:_*);
  }

  def register(bindings:BindingInfo[_,_]*) = {
    _bindings ++= bindings.map(b => { 
      b.kernel = this
      b.source -> b 
    })
  }

  def get[T](implicit man:Manifest[T]):Option[T] = {
    findBinding[T] match {
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

  private def findBinding[T](implicit man:Manifest[T]):Option[BindingInfo[_,_]] = {
    _bindings.get(man) match {
      case Some(b) => Some(b.asInstanceOf[BindingInfo[_,_]])
      case None => {
        if (man.typeArguments.size == 0) {
          None
        } else {
          _bindings.find(m => m._1.typeArguments.forall(_.erasure == classOf[Any]) && m._1 >:> man) match {
            case None => None
            case Some(x) => {
              _bindings += man -> x._2.asInstanceOf[BindingInfo[_,_]]
              Some(x._2.asInstanceOf[BindingInfo[_,_]])
            }
          }
        }
      }
    }
  }
}

object Kernel {
  def apply():Kernel = new StandardKernel()
}

