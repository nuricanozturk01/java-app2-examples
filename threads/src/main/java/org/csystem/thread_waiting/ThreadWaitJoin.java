package org.csystem.thread_waiting;

import com.karandev.util.console.Console;
import lombok.Data;

import java.util.Random;

public class ThreadWaitJoin {
  private static final int MIN = 1, MAX = 2, THREAD_COUNT = 10, COUNT = 50;

  private static void join(Thread thread) {
    try {
      thread.join();
    } catch (InterruptedException ignore) {

    }
  }

  public static void run() {
    final var threads = new Thread[THREAD_COUNT];
    final var params = new ThreadParams[THREAD_COUNT];

    for (int i = 0; i < THREAD_COUNT; i++) {
      int idx = i;

      final var threadName = "Thread-%d".formatted(idx + 1);
      params[i] = new ThreadParams(threadName);
      threads[i] = new Thread(() -> ThreadWaitJoin.findTotalCallback(params[idx]), threadName);

      threads[i].start();
    }

    for (Thread t : threads)
      join(t);

    // BARRIER
    for (var tp : params)
      Console.writeLine(tp);
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
    ThreadWaitJoin.run();
  }
}


