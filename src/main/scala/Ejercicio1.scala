package concurrencia

import java.util.concurrent.*
import scala.util.Random

object mediciones {
  // CS-Sensor-i: sensor i no puede volver a medir hasta que el trabajador no ha
  // terminado de procesar las medidas anteriores
  // CS-Trabajador: no puede realizar su tarea hasta que no están las
  // tres mediciones
  private val trabajando = new Semaphore(0)
  private val midiendo = new Semaphore(1)
  private val mutex = new Semaphore(1)
  private var mediciones = 0

  def nuevaMedicion(id: Int): Unit = {
    midiendo.acquire()
    mutex.acquire()
    mediciones += 1
    log(s"Sensor $id almacena su medición" )
    if mediciones == 3 then
      trabajando.release()
    else
      midiendo.release()
    mutex.release()
  }

  def leerMediciones(): Unit = {
    trabajando.acquire()
    mutex.acquire()
    log(s"El trabajador recoge las mediciones")
  }

  def finTarea(): Unit = {
    mediciones = 0
    midiendo.release()
    log(s"El trabajador ha terminado sus tareas")
    mutex.release()
  }
}

object Ejercicio1 {
  def main(args: Array[String]) =
    val sensor = new Array[Thread](3)

    for (i <- sensor.indices)
      sensor(i) = thread {
        while (true)
          Thread.sleep(Random.nextInt(100)) // midiendo
          mediciones.nuevaMedicion(i)
      }

    val trabajador = thread {
      while (true)
        mediciones.leerMediciones()
        Thread.sleep(Random.nextInt(100)) // realizando la tarea
        mediciones.finTarea()
    }
}
