package org.csystem.interrupt_status;

import com.karandev.io.util.console.Console;
import org.csystem.util.concurrent.ThreadUtil;

import java.util.concurrent.TimeUnit;


/**
 * {@code isInterrupted()} metodu, ilgili thread’in interrupt bayrağını kontrol eder fakat sıfırlamaz. Bu nedenle threadCallback1
 * metodundaki ilk döngü {@code isInterrupted()} ile çalışırken, thread interrupt edildiğinde bayrak true olur ve döngü sonlanır.
 * Ancak bayrak sıfırlanmadığı için ikinci while döngüsü hiçbir zaman çalışmaz.
 * <p>
 * Öte yandan {@code Thread.interrupted()} metodu, mevcut thread’in interrupt bayrağını hem kontrol eder hem de sıfırlar.
 * Bu nedenle threadCallback2 metodundaki ilk döngü sırasında interrupt edildiğinde, bayrak kontrol edilip sıfırlanır.
 * Böylece ikinci döngüye geçildiğinde interrupt bayrağı tekrar false olduğu için döngü çalışmaya devam eder.
 * <p>
 * Ayrıca, t2 thread’i {@code Thread.interrupted()} metodunun çağrısıyla bayrağı sıfırladığı için ikinci kez interrupt()
 * çağrıldığında bayrak yeniden true olur ve bu, ikinci döngüyü sonlandırabilir
 */
@SuppressWarnings("all")
class ThreadInterruptStatus {
  private static void threadCallback1() {
    var a = 0L;
    var self = Thread.currentThread();

    while (!self.isInterrupted())
      Console.writeLine("t1->First:%d", a++);

    while (!self.isInterrupted())
      Console.writeLine("t1->Second:%d", a++);
  }

  private static void threadCallback2() {
    var a = 0L;

    while (!Thread.interrupted())
      Console.writeLine("t2->First:%d", a++);

    while (!Thread.interrupted())
      Console.writeLine("t2->Second:%d", a++);
  }

  public static void run(String[] args) {
    var t1 = new Thread(ThreadInterruptStatus::threadCallback1);
    var t2 = new Thread(ThreadInterruptStatus::threadCallback2);

    t1.start();
    t2.start();

    ThreadUtil.sleep(3, TimeUnit.SECONDS);
    t1.interrupt();
    t2.interrupt();
    ThreadUtil.sleep(3, TimeUnit.SECONDS);
    t2.interrupt();
  }


  public static void main(String[] args) {
    ThreadInterruptStatus.run(args);
  }
}

