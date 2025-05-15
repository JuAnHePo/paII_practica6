package concurrencia

import java.util.concurrent.*
import scala.util.Random

object aseo {
  // CS-Cliente: Esperan si está el Equipo de Limpieza en el aseo
  // CS-EquipoLimpieza: Espera si hay clientes en el aseo
  private var numClientes = 0
  private val mutex = Semaphore(1)
  private val limpiando = Semaphore(0)
  private val aseando = Semaphore(1)

  def entraCliente(id:Int): Unit = {
    aseando.acquire()
    mutex.acquire()
    numClientes += 1
    log(s"Entra cliente $id. Hay $numClientes clientes.")
    mutex.release()
    aseando.release()
  }

  def saleCliente(id:Int): Unit = {
    aseando.acquire()
    mutex.acquire()
    numClientes -= 1
    log(s"Sale cliente $id. Hay $numClientes clientes.")
    if numClientes == 0 then
      limpiando.release()
      mutex.release()
    else
      aseando.release()
      mutex.release()
  }

  def entraEquipoLimpieza(): Unit = {
    limpiando.acquire()
    mutex.acquire()
    log(s"        Entra el equipo de limpieza.")
  }

  def saleEquipoLimpieza(): Unit = {
    log(s"        Sale el equipo de limpieza.")
    mutex.release()
    aseando.release()
  }
}

object Ejercicio3 {
  def main(args: Array[String]): Unit = {
    val cliente = new Array[Thread](10)
    for (i <- cliente.indices)
      cliente(i) = thread {
        while (true)
          Thread.sleep(Random.nextInt(500))
          aseo.entraCliente(i)
          Thread.sleep(Random.nextInt(50))
          aseo.saleCliente(i)
      }
    val equipoLimpieza = thread {
      while (true)
        Thread.sleep(Random.nextInt(500))
        aseo.entraEquipoLimpieza()
        Thread.sleep(Random.nextInt(100))
        aseo.saleEquipoLimpieza()
    }
  }
}
