package concurrencia

import java.util.concurrent.*
import scala.util.Random

class Cadena(n: Int) {
  // CS-empaquetador-i: espera hasta que hay productos de tipo i
  // CS-colocador: espera si hay n productos en la cadena
  private val tipo = Array.fill(3)(0) // el buffer
  private var cuentaTotal = 0
  private val esperaCol = Semaphore(1) // CS- Colocador
  private val empaquetadores = Array.fill(3)(Semaphore(1))

  def retirarProducto(p: Int): Unit = {
    if tipo(p) != 0 then
      empaquetadores(p).acquire()
      tipo(p) -= 1
      cuentaTotal += 1
      log(s"Empaquetador $p retira un producto. Quedan ${tipo.mkString("[", ",", "]")}")
      empaquetadores(p).release()
  }

  def nuevoProducto(p:Int): Unit = {
    if !cadenaLlena() then
      empaquetadores(p).acquire()
      tipo(p) += 1
      log(s"Colocador pone un producto $p. Quedan ${tipo.mkString("[", ",", "]")}")
      log(s"Total de productos empaquetados $cuentaTotal")
      empaquetadores(p).release()
  }

  private def cadenaLlena(): Boolean = {
    var prodColocados = 0
    for (i <- tipo.indices) prodColocados += tipo(i)
    prodColocados == 5
  }
}

object Ejercicio2 {
  def main(args:Array[String]) = {
    val cadena = new Cadena(6)
    val empaquetador = new Array[Thread](3)
    for (i <- empaquetador.indices)
      empaquetador(i) = thread {
        while (true)
          cadena.retirarProducto(i)
          Thread.sleep(Random.nextInt(1000)) // empaquetando
      }

    val colocador = thread {
      while (true)
        Thread.sleep(Random.nextInt(100)) // recogiendo el producto
        cadena.nuevoProducto(Random.nextInt(3))
    }
  }
}
