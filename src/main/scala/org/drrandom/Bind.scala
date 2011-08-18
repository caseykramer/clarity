package org.drrandom

import scala.None
import java.lang.Class
import java.lang.reflect._
import reflect.{Manifest}


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


class Bind[T](implicit val sourceType:Manifest[T]) extends TypeBinder[T] {

}

object Bind {
  def apply[T](implicit man:Manifest[T]) = new Bind[T]()
}