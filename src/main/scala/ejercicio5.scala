package concurrencia

import java.util.concurrent.*
import scala.util.Random

object gestorAgua {
  // CS-Hid1: El hidrógeno que quiere formar una molécula espera si ya hay dos hidrógenos
  // CS-Hid2: Un hidrógeno debe esperar a los otros dos átomos para formar la molécula
  // CS-Ox1: El oxígeno que quiere formar una molécula espera si ya hay un oxígeno
  // CS-Ox2: El oxígeno debe esperar a los otros dos átomos para formar la molécula
  private val mutex = Semaphore(1)
  private val hidrogeno = Semaphore(1)
  private val oxigeno = Semaphore(1)
  private var nHidrogenos = 0
  private var hayOxigeno = false

  def oxigeno(id: Int): Unit = {
    // el oxígeno id quiere formar una molécula
    oxigeno.acquire()
    mutex.acquire()
    log(s"Oxígeno $id quiere formar una molécula")
    if nHidrogenos != 2 then hayOxigeno = true
    else
      nHidrogenos = 0
      log(s"      Molécula formada!!!")
      hidrogeno.release()
      oxigeno.release()
    mutex.release()
  }

  def hidrogeno(id: Int): Unit = {
    // el hidrógeno id quiere formar una molécula
    hidrogeno.acquire()
    mutex.acquire()
    log(s"Hidrógeno $id quiere formar una molécula")
    if nHidrogenos == 0 then
      nHidrogenos += 1
      hidrogeno.release()
    else if nHidrogenos == 1 && !hayOxigeno then
      nHidrogenos += 1
    else if nHidrogenos == 1 && hayOxigeno then
      nHidrogenos = 0
      hayOxigeno = false
      log(s"      Molécula formada!!!")
      hidrogeno.release()
      oxigeno.release()
    mutex.release()
  }
}

object Ejercicio5 {
  def main(args:Array[String]): Unit =
    val N = 5
    val hidrogeno = new Array[Thread](2*N)
    for (i<- hidrogeno.indices)
      hidrogeno(i) = thread{
        Thread.sleep(Random.nextInt(500))
        gestorAgua.hidrogeno(i)
      }
    val oxigeno = new Array[Thread](N)
    for(i <- oxigeno.indices)
      oxigeno(i) = thread {
        Thread.sleep(Random.nextInt(500))
        gestorAgua.oxigeno(i)
      }
    hidrogeno.foreach(_.join())
    oxigeno.foreach(_.join())
    log("Fin del programa")
}
