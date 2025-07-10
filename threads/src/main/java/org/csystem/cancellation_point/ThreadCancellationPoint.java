package org.csystem.cancellation_point;

import com.karandev.util.console.Console;
import org.csystem.util.concurrent.ThreadUtil;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * <h3>Thread Cancellation Point</h3>
 * Thread sınıfının {@code join()}, Executor servisinin {@code get()}
 * ({@code Future.get()}), {@code Thread.sleep()}, {@code Object.wait()}
 * gibi metotları <strong>Thread Cancellation Point</strong> metodlarıdır.
 * Bu metotlar {@code InterruptedException} fırlatarak thread'in graceful
 * shutdown mekanizmasını destekler. (interrupt flag değerini temizler)
 *
 * <h3>threadCallback1 Metodu</h3>
 * Bu metotta try-catch bloğu while döngüsünü sarmaladığı için, thread
 * interrupt edildiğinde {@code TimeUnit.MILLISECONDS.sleep()} metodu
 * {@code InterruptedException} fırlatır ve catch bloğu yakalanarak thread
 * düzgün bir şekilde sonlanır.
 *
 * <h3>threadCallback2 Metodu:</h3>
 * Bu metotta try-catch bloğu while döngüsünün içerisinde yer alır.
 * <ol>
 *   <li><strong>İlk interrupt</strong> geldiğinde {@code InterruptedException}
 *       yakalanır ve "interrupt!..." mesajı yazdırılır</li>
 *   <li><strong>Kritik nokta</strong>: {@code InterruptedException} yakalandığında,
 *       Java'nın interrupt flag'i <strong>otomatik olarak temizlenir</strong>
 *       (clear edilir)</li>
 *   <li>Bu nedenle sonraki interrupt çağrıları thread'in interrupt durumunu
 *       kontrol etmez ve döngü devam eder</li>
 *   <li>İkinci {@code t2.interrupt()} çağrısında thread artık interrupt
 *       durumunda olmadığı için sleep metodu normal çalışmaya devam eder</li>
 * </ol>
 */
class ThreadCancellationPoint {
  private static void threadCallback1() {
    var random = new Random();
    var a = 0L;

    try {
      while (true) {
        TimeUnit.MILLISECONDS.sleep(random.nextInt(500, 1001));
        Console.writeLine("thread-1:%d", a++);
      }
    } catch (InterruptedException ignore) {
      Console.writeLine("thread-1 ends!...");
    }
  }

  private static void threadCallback2() {
    var random = new Random();
    var a = 0L;

    while (true) {
      try {
        TimeUnit.MILLISECONDS.sleep(random.nextInt(500, 1001));
        Console.writeLine("thread-2:%d", a++);
      } catch (InterruptedException ignore) {
        Console.writeLine("interrupt!...");
      }
    }
  }

  public static void run(String[] args) {
    var t1 = new Thread(ThreadCancellationPoint::threadCallback1);
    var t2 = new Thread(ThreadCancellationPoint::threadCallback2);

    t1.start();
    t2.start();

    ThreadUtil.sleep(5, TimeUnit.SECONDS);
    t1.interrupt();
    t2.interrupt();
    ThreadUtil.sleep(5, TimeUnit.SECONDS);
    t2.interrupt();
  }

  public static void main(String[] args) {
    ThreadCancellationPoint.run(args);
  }
}

