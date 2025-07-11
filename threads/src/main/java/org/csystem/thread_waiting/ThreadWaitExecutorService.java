package org.csystem.thread_waiting;

import com.karandev.util.console.Console;
import lombok.Data;

import java.security.SecureRandom;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@SuppressWarnings("all")
public class ThreadWaitExecutorService {
  private static final int MIN = -5_000, MAX = 5_000, THREAD_COUNT = 100, COUNT = 1500;

  private static void join(Future<?> future) {
    try {
      future.get();
    } catch (ExecutionException | InterruptedException ignore) {

    }
  }

  public static void run() {
    final var threadPool = Executors.newFixedThreadPool(THREAD_COUNT);
    final var futures = new Future[THREAD_COUNT];
    final var params = new ThreadParams[THREAD_COUNT];

    for (int i = 0; i < THREAD_COUNT; i++) {
      int idx = i;

      final var threadName = "Thread-%d".formatted(idx + 1);
      params[i] = new ThreadParams(threadName);

      futures[i] = threadPool.submit(() -> ThreadWaitExecutorService.findTotalCallback(params[idx], System.currentTimeMillis()));
    }

    for (var future : futures)
      join(future);

    // BARRIER
    int negativeCount = 0;

    for (var tp : params)
      if (tp.result > 0)
        Console.writeLine(tp);
      else {
        negativeCount++;
        Console.writeLine(tp);
      }

    Console.writeLine("Positive: " + (THREAD_COUNT - negativeCount) + " Negative: " + negativeCount);
    threadPool.shutdown();

  }

  private static void findTotalCallback(final ThreadParams param, long t) {
    final var random = new Random(t);

    for (int i = 0; i < COUNT; i++)
      param.add(random.nextInt(MIN, MAX));
  }

  @Data
  private static final class ThreadParams {
    private final String name;
    private int result;

    @Override
    public String toString() {
      return "Name: %s, Result: %d".formatted(this.name, this.result);
    }

    public void add(final int number) {
      this.result += number;
    }
  }

  public static void main(String[] args) {
    ThreadWaitExecutorService.run();
  }
}


