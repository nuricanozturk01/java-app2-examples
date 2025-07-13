package org.csystem.producer_consumer;

import com.karandev.io.util.console.Console;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

/**
 * Producer-Consumer Pattern Implementation with Binary Semaphore
 * <p>
 * Bu sınıf, iki binary semaphore kullanarak Producer-Consumer probleminini
 * çözümünü göstermektedir. Semaphore'lar arasındaki coordination ile
 * thread'lerin sıralı çalışması sağlanmaktadır.
 *
 * <h3>Semaphore Nedir?</h3>
 * Semaphore, bir kritik bölgeye <strong>N tane thread'in girebileceği</strong>,
 * <strong>N+1. thread'in giremeyeceğini</strong> kontrol eden synchronization
 * nesnesidir.
 *
 * <h4>Semaphore Özellikleri:</h4>
 * <ul>
 *   <li><strong>No Ownership:</strong> Herhangi bir thread permit bırakabilir
 *       (güvenlik riski)</li>
 *   <li><strong>Non-Reentrant:</strong> Aynı thread bir daha acquire yapamaz
 *       (deadlock riski)</li>
 *   <li><strong>Counting:</strong> Birden fazla kaynak yönetimi</li>
 *   <li><strong>Indirect Communication:</strong> Ortak permit havuzu kullanır</li>
 * </ul>
 *
 * <h3>Mutex vs Semaphore Karşılaştırması</h3>
 *
 * <h4>Mutual Exclusion (synchronized/mutex):</h4>
 * <ul>
 *   <li><strong>Ownership VAR:</strong> Kilidi alan thread bırakmalı</li>
 *   <li><strong>Reentrant:</strong> Aynı thread tekrar girebilir</li>
 *   <li><strong>Direct Communication:</strong> Thread'ler birbirine sinyal verir</li>
 *   <li><strong>Kullanım:</strong> Data Protection</li>
 *   <li><strong>Kapasite:</strong> 1 thread ile sınırlı</li>
 * </ul>
 *
 * <h4>Semaphore:</h4>
 * <ul>
 *   <li><strong>Ownership YOK:</strong> Herhangi bir thread permit bırakabilir</li>
 *   <li><strong>Non-Reentrant:</strong> Aynı thread tekrar acquire yapamaz</li>
 *   <li><strong>Indirect Communication:</strong> Ortak permit havuzu</li>
 *   <li><strong>Kullanım:</strong> Resource Management</li>
 *   <li><strong>Kapasite:</strong> N thread</li>
 * </ul>
 *
 * <h3>Neden synchronized Yeterli Değil?</h3>
 * {@code synchronized(this)} bu durumda çözüm değildir çünkü:
 * <ol>
 *   <li><strong>Coordination problemi:</strong> Mutex sadece mutual exclusion sağlar,
 *       coordination sağlamaz</li>
 *   <li><strong>Race condition:</strong> Producer bir değer yazdıktan sonra
 *       consumer'ın o değeri okumasını garanti etmez</li>
 *   <li><strong>Data loss:</strong> Consumer aynı değeri birden fazla kez okuyabilir
 *       veya bazı değerleri kaçırabilir</li>
 *   <li><strong>Uncontrolled execution:</strong> Producer'ın yeni değer yazmadan
 *       consumer'ın beklemesini sağlamaz</li>
 * </ol>
 *
 * <h3>Binary Semaphore ile Coordination</h3>
 * Bu implementasyonda iki binary semaphore kullanılır:
 * <ul>
 *   <li><strong>m_semaphoreProducer:</strong> Başlangıç değeri 1
 *       (Producer'ın ilk başlayacağını belirtir)</li>
 *   <li><strong>m_semaphoreConsumer:</strong> Başlangıç değeri 0
 *       (Consumer'ın bekleyeceğini belirtir)</li>
 * </ul>
 *
 * <h4>Çalışma Mantığı:</h4>
 * <pre>{@code
 * 1. Producer: m_semaphoreProducer.acquire() → Permit alır, veri yazar
 * 2. Producer: m_semaphoreConsumer.release() → Consumer'ı uyandırır
 * 3. Consumer: m_semaphoreConsumer.acquire() → Permit alır, veri okur
 * 4. Consumer: m_semaphoreProducer.release() → Producer'ı uyandırır
 * 5. Döngü devam eder...
 * }</pre>
 *
 * <h3>Java Semaphore Constructor</h3>
 * {@code Semaphore(int permits, boolean fair)} constructor'ı:
 * <ul>
 *   <li><strong>permits:</strong> Kritik bölgeye kaç thread'in girebileceği</li>
 *   <li><strong>fair:</strong> Thread sıralaması kontrol eder
 *       <ul>
 *         <li>{@code false:} O anki scheduling algoritmasına göre belirlenir</li>
 *         <li>{@code true:} FIFO sıralaması (hangi sırada beklemeye girdi ise
 *             o sırada kritik bölgeye girer)</li>
 *       </ul>
 *   </li>
 * </ul>
 *
 * <h3>Semaphore Metotları</h3>
 * <ul>
 *   <li><strong>acquire():</strong> Permit sayısını 1 azaltır
 *       <ul><li>Sayaç sıfırdan farklı ise thread girer ve sayacı 1 azaltır</li></ul>
 *   </li>
 *   <li><strong>acquire(int permits):</strong> Permit sayısını belirtilen miktar azaltır
 *       <ul><li>Verilen sayı kadar thread'in kritik bölgeye girmesine izin verir</li></ul>
 *   </li>
 *   <li><strong>release():</strong> Permit sayısını 1 artırır</li>
 *   <li><strong>release(int permits):</strong> Permit sayısını belirtilen miktar artırır</li>
 * </ul>
 *
 * <p><strong>Önemli Not:</strong> Semaphore'da başka bir thread tarafından kritik bölge
 * kontrolü yapılabilir (No Ownership özelliği nedeniyle).
 */
@SuppressWarnings("all")
public class ProducerConsumerBinarySemaphore {
  private final ExecutorService m_executorService = Executors.newFixedThreadPool(2);

  // Buradaki 1 ilk producer'in baslayacagini belirtir
  private final Semaphore m_semaphoreProducer = new Semaphore(1);
  private final Semaphore m_semaphoreConsumer = new Semaphore(0);
  private int m_value = 0;

  // Trenin gecebilmesi icin sayacin bir olmasi lazim
  private Void producerCallback() throws InterruptedException {
    var random = new Random();
    var value = 0;

    while (true) {
      m_semaphoreProducer.acquire(1);
      m_value = value++;
      m_semaphoreConsumer.release();

      if (value == 101)
        break;

      Thread.sleep(random.nextLong(10, 200));
    }

    return null;
  }

  private Void consumerCallback() throws InterruptedException {
    var random = new Random();
    int value;

    while (true) {
      m_semaphoreConsumer.acquire();
      value = m_value;
      m_semaphoreProducer.release();

      Console.write("%d ", value);
      if (value >= 100)
        break;

      Thread.sleep(random.nextLong(10, 200));
    }

    return null;
  }

  public void run() {
    m_executorService.submit(this::producerCallback);
    m_executorService.submit(this::consumerCallback);
    m_executorService.shutdown();
  }

  public static void main(String[] args) {
    ProducerConsumerBinarySemaphore producer = new ProducerConsumerBinarySemaphore();
    producer.run();
  }
}
