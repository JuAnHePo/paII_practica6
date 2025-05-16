package concurrencia

import java.util.concurrent.*
import scala.util.Random

class Olla(R: Int) {
  // CS-caníbal i: no coge una ración de la olla si está vacía
  // CS-cocinero: no cocina un nuevo explorador hasta que la olla está vacía
  private var olla = R // inicialmente llena
  private val mutex = Semaphore(1)
  private val cocinar = Semaphore(0)
  private val comer = Semaphore(1)

  def racion(i: Int): Unit = {
    // caníbal i coge una ración de la olla
    comer.acquire()
    mutex.acquire()
    olla -= 1
    log(s"Caníbal $i coge una ración de la olla. Quedan $olla raciones.")
    if olla == 0 then cocinar.release() else comer.release()
    mutex.release()
  }

  def dormir(): Unit = {
    // cocinero espera a que la olla esté vacía
    cocinar.acquire()
  }

  def llenarOlla(): Unit = {
    mutex.acquire()
    olla = R
    log(s"El cocinero llena la olla. Quedan $olla raciones.")
    comer.release()
    mutex.release()
  }
}

object Ejercicio8 {
  def main(args: Array[String]): Unit =
    val NCan = 20
    val olla = new Olla(5)
    val canibal = new Array[Thread](NCan)
    for (i <- canibal.indices)
      canibal(i) = thread {
        while (true)
          Thread.sleep(Random.nextInt(500))
          olla.racion(i)
      }
    val cocinero = thread {
      while (true)
        olla.dormir()
        Thread.sleep(500) // cocinando
        olla.llenarOlla()
    }
}
