package org.csystem.producer_consumer;

import com.karandev.util.console.Console;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

@SuppressWarnings("all")
public class ProducerConsumerArrayDeque {
  private static final int QUEUE_SIZE = 10;
  private final ExecutorService m_executorService = Executors.newFixedThreadPool(2);
  private final Semaphore m_semaphoreProducer = new Semaphore(QUEUE_SIZE);
  private final Semaphore m_semaphoreConsumer = new Semaphore(0);
  private final Deque<Integer> m_queue;

  private Void producerCallback() throws InterruptedException {
    var random = new Random();
    var value = 0;

    while (true) {
      m_semaphoreProducer.acquire(QUEUE_SIZE);
      m_queue.push(value++);
      m_semaphoreConsumer.release(QUEUE_SIZE);

      if (value == 100)
        break;

      Thread.sleep(random.nextLong(10, 200));
    }

    return null;
  }

  private Void consumerCallback() throws InterruptedException {
    var random = new Random();
    int value;

    while (true) {
      m_semaphoreConsumer.acquire(QUEUE_SIZE);
      value = m_queue.removeFirst();
      m_semaphoreProducer.release(QUEUE_SIZE);

      Console.write("%d ", value);
      if (value >= 99)
        break;

      Thread.sleep(random.nextLong(10, 200));
    }

    return null;
  }

  public ProducerConsumerArrayDeque(Deque<Integer> queue) {
    m_queue = queue;
  }

  public void run() {
    m_executorService.submit(this::producerCallback);
    m_executorService.submit(this::consumerCallback);
    m_executorService.shutdown();
  }

  public static void main(String[] args) {
    var pc = new ProducerConsumerArrayDeque(new ArrayDeque<>());

    pc.run();
  }
}
