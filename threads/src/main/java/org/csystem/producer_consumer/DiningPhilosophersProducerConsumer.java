package org.csystem.producer_consumer;

import com.karandev.util.console.Console;
import org.csystem.producer_consumer.model.Philosopher;
import org.csystem.util.concurrent.ThreadUtil;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * Dining Philosophers Problem - Producer-Consumer Pattern çözümü.
 *
 * <p>Bu çözümde klasik "Dining Philosophers Problem" {@code Producer-Consumer Pattern} yaklaşımı ile ele alınmıştır.
 * Filozoflar, yemek yeme talepleri oluşturan üreticiler (Producers) olarak modellenmiştir.
 * Tüm talepler {@code ArrayDeque} yapısında bir istek kuyruğunda (RequestQueue) toplanır.
 * Tek bir garson (Waiter) bu talepleri sırayla işleyerek çatalların dağıtımını gerçekleştirir ve
 * aynı anda en fazla iki filozofun yemek yemesine izin verir. Böylece deadlock durumu önlenmiş olur.
 *
 * <p>Bu model aşağıdaki sınıflardan oluşur:
 *
 * <ul>
 *     <li>{@link Philosopher} - Yemek isteği oluşturan ve yemek yeme simülasyonu yapan üretici.</li>
 *     <li>{@link Waiter} - Talepleri sırayla işleyen tüketici, çatal kaynaklarını yönetir.</li>
 *     <li>{@link Request} - Her filozofun yemek isteğini temsil eden nesne.</li>
 *     <li>{@link DiningTable} - Filozofların ve garsonun senkronize çalıştığı ortam.</li>
 * </ul>
 */

@SuppressWarnings("all")
public class DiningPhilosophersProducerConsumer {
  private static final int PHILOSOPHER_COUNT = 5;
  private static final int WAITER_COUNT = 1;
  private static final int REQUEST_QUEUE_SIZE = 10;
  private final ExecutorService m_threadPool;
  private final Deque<Philosopher> m_philosophers;
  private final Deque<Philosopher> m_requestQueue;
  private final Semaphore[] m_forkSemaphores;
  private final Semaphore[] m_waitingPermitPhilosophers;
  private final Semaphore m_pendingRequests;
  private final Semaphore m_requestSlots;
  private final Object m_queueLock = new Object();

  public DiningPhilosophersProducerConsumer() {
    m_threadPool = Executors.newFixedThreadPool(PHILOSOPHER_COUNT + WAITER_COUNT);
    m_philosophers = new LinkedList<>();
    m_requestQueue = new ArrayDeque<>();
    m_forkSemaphores = new Semaphore[PHILOSOPHER_COUNT];
    m_waitingPermitPhilosophers = new Semaphore[PHILOSOPHER_COUNT];
    m_pendingRequests = new Semaphore(0);
    m_requestSlots = new Semaphore(REQUEST_QUEUE_SIZE);

    for (int i = 0, philosopherName = 'A'; i < PHILOSOPHER_COUNT; i++) {
      m_philosophers.addLast(new Philosopher("Philosopher-" + (char) philosopherName++, i));
      m_forkSemaphores[i] = new Semaphore(1);
      m_waitingPermitPhilosophers[i] = new Semaphore(0);
    }

    Console.writeLine("Dining room initialized: %d philosophers, %d waiter",
            PHILOSOPHER_COUNT, WAITER_COUNT);
  }

  private void think(final Philosopher philosopher) {
    Console.writeLine("%s is thinking", philosopher.name());
    ThreadUtil.sleep(50, TimeUnit.MILLISECONDS);
  }

  private void requestEating(final Philosopher philosopher) throws InterruptedException {
    Console.writeLine("%s request for eating", philosopher.name());

    m_requestSlots.acquire();
    synchronized (m_queueLock) {
      m_requestQueue.addLast(philosopher);
    }
    m_pendingRequests.release();

    m_waitingPermitPhilosophers[philosopher.place()].acquire();
    Console.writeLine("%s got permission to eat", philosopher.name());
  }

  private void eat(final Philosopher philosopher) {
    Console.writeLine("%s is eating", philosopher.name());
    ThreadUtil.sleep(50, TimeUnit.MILLISECONDS);
  }

  private void finishEating(final Philosopher philosopher) {
    int leftFork = philosopher.place();
    int rightFork = (philosopher.place() + 1) % PHILOSOPHER_COUNT;

    int firstFork = Math.min(leftFork, rightFork);
    int secondFork = Math.max(leftFork, rightFork);

    m_forkSemaphores[secondFork].release();
    m_forkSemaphores[firstFork].release();

    Console.writeLine("%s finished eating - returned forks %d and %d", philosopher.name(), firstFork, secondFork);
  }

  private void processPhilosopherRequest(final Philosopher philosopher) throws InterruptedException {
    Console.writeLine("Waiter processing request from %s", philosopher.name());

    int leftFork = philosopher.place();
    int rightFork = (philosopher.place() + 1) % PHILOSOPHER_COUNT;

    int firstFork = Math.min(leftFork, rightFork);
    int secondFork = Math.max(leftFork, rightFork);

    m_forkSemaphores[firstFork].acquire();
    m_forkSemaphores[secondFork].acquire();

    Console.writeLine("Waiter gave forks %d and %d to %s", firstFork, secondFork, philosopher.name());

    m_waitingPermitPhilosophers[philosopher.place()].release();
  }

  private Void waiterCallback() throws InterruptedException {
    Console.writeLine("Waiter started service");

    for (int processedRequests = 0; processedRequests < PHILOSOPHER_COUNT; processedRequests++) {
      m_pendingRequests.acquire();

      final Philosopher philosopher;
      synchronized (m_queueLock) {
        philosopher = m_requestQueue.removeFirst();
      }
      m_requestSlots.release();

      processPhilosopherRequest(philosopher);
    }

    Console.writeLine("Waiter completed service");
    return null;
  }

  private Void philosopherCallback(final Philosopher philosopher) throws InterruptedException {
    think(philosopher);
    requestEating(philosopher);
    eat(philosopher);
    finishEating(philosopher);

    Console.writeLine("%s completed dining session", philosopher.name());
    return null;
  }

  public void run() throws InterruptedException {
    Console.writeLine("Starting dining simulation...");

    m_philosophers.forEach(philosopher -> m_threadPool.submit(() -> philosopherCallback(philosopher)));

    m_threadPool.submit(this::waiterCallback);

    m_threadPool.shutdown();

    if (m_threadPool.awaitTermination(60, TimeUnit.SECONDS)) {
      Console.Error.writeLine("Dining simulation completed successfully");
    }
  }

  public static void main(String[] args) {
    try {
      final var diningSimulation = new DiningPhilosophersProducerConsumer();
      diningSimulation.run();
    } catch (InterruptedException e) {
      Console.Error.writeLine("Application interrupted");
      Thread.currentThread().interrupt();
    } catch (Exception e) {
      Console.Error.writeLine("Unexpected error: %s", e.getMessage());
    }
  }
}