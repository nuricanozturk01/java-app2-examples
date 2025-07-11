package org.csystem.thread_waiting;

import com.karandev.util.console.Console;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import static java.lang.System.currentTimeMillis;

/**
 * Callable Interface ve Future Kullanımı Demonstrasyonu
 * <p>
 * Bu sınıf {@code Callable} interface'inin {@code Runnable} interface'ine göre
 * avantajlarını ve {@code Future} ile entegrasyonunu göstermektedir.
 *
 * <h3>Callable Interface'inin Özellikleri:</h3>
 * <ol>
 *   <li><strong>Değer Döndürme:</strong> {@code call()} metodu generic bir değer döndürebilir</li>
 *   <li><strong>Exception Fırlatma:</strong> {@code call()} metodu checked exception fırlatabilir</li>
 *   <li><strong>Future ile Entegrasyon:</strong> {@code ExecutorService.submit()} metodu ile
 *       kullanıldığında {@code Future} objesi döndürür</li>
 * </ol>
 *
 * <h3>Bu Kodda Callable Kullanımı:</h3>
 * <ul>
 *   <li>Lambda expression {@code ThreadWaitCallableInterface::findTotalCallback}
 *       bir {@code Callable<Long>} implement eder</li>
 *   <li>{@code submit()} metodu bu callable'ı çalıştırır ve {@code Future<Long>} döndürür</li>
 *   <li>{@code Future.get()} metodu ile thread'in sonucunu (long) alırız</li>
 * </ul>
 *
 * <h3>Future Interface'inin Faydaları:</h3>
 * <ol>
 *   <li><strong>Asenkron Sonuç Alma:</strong> Thread'in işini bitirmesini bekler ve sonucu döndürür</li>
 *   <li><strong>Exception Handling:</strong> Thread içinde oluşan exception'ları
 *       {@code ExecutionException} olarak wrap eder</li>
 *   <li><strong>Cancellation:</strong> {@code cancel()} metodu ile thread'i iptal edebilir</li>
 *   <li><strong>Status Kontrolü:</strong> {@code isDone()}, {@code isCancelled()} metotları ile
 *       durumu kontrol edebilir</li>
 * </ol>
 */
@SuppressWarnings("all")
public class ThreadWaitCallableInterface {
  private static final int MIN = 1, MAX = 2, THREAD_COUNT = 100, COUNT = 50;

  public static void run() {
    final var threadPool = Executors.newFixedThreadPool(THREAD_COUNT);
    final var futures = new ArrayList<Future<Long>>(THREAD_COUNT);

    for (int i = 0; i < THREAD_COUNT; i++) {
      futures.add(threadPool.submit(ThreadWaitCallableInterface::findTotalCallback));
    }

    try {
      for (var future : futures)
        Console.writeLine("Result: %d", future.get());
    } catch (ExecutionException | InterruptedException ignored) {
    }

    threadPool.shutdown();
  }

  private static long findTotalCallback() {
    final var random = new Random();

    return LongStream.range(0, COUNT)
            .map(x -> random.nextInt(MIN, MAX))
            .sum();
  }

  public static void main(String[] args) {
    ThreadWaitCallableInterface.run();
  }
}


