package org.csystem.thread_waiting;

import com.karandev.util.console.Console;
import lombok.Data;

import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ThreadWaitExecutorService {
  private static final int MIN = 1, MAX = 2, THREAD_COUNT = 100, COUNT = 50;

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

      futures[i] = threadPool.submit(() -> ThreadWaitExecutorService.findTotalCallback(params[idx]));
    }

    for (var future : futures)
      join(future);

    for (var tp : params)
      Console.writeLine(tp);

    threadPool.shutdown();
  }

  private static void findTotalCallback(final ThreadParams param) {
    final var random = new Random();

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


