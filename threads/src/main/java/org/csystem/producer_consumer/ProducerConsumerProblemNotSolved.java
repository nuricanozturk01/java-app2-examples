package org.csystem.producer_consumer;

import com.karandev.io.util.console.Console;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SuppressWarnings("all")
public class ProducerConsumerProblemNotSolved {
  private final ExecutorService m_executorService = Executors.newFixedThreadPool(2);
  private int m_value = -1;

  private Void producerCallback() throws InterruptedException {
    var random = new Random();
    var value = 0;

    while (true) {
      m_value = value++;

      if (value == 100)
        break;

      Thread.sleep(random.nextLong(10, 200));
    }

    return null;
  }

  private Void consumerCallback() throws InterruptedException {
    var random = new Random();

    while (true) {
      var value = m_value;

      Console.write("%d ", value);
      if (value >= 99)
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
    final var producer = new ProducerConsumerProblemNotSolved();
    producer.run();
  }
}
