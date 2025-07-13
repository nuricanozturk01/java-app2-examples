package org.csystem.udp.udp_util;

import com.karandev.io.util.console.Console;
import com.karandev.util.net.UdpUtil;
import org.csystem.util.string.StringUtil;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static java.util.concurrent.TimeUnit.SECONDS;

public class Sender {
  private final ScheduledExecutorService scheduledExecutorService;
  private final String host;
  private final int port;
  private final Random random;

  public Sender(final String host, final int port) {
    this.host = host;
    this.port = port;
    this.scheduledExecutorService = Executors.newScheduledThreadPool(1);
    this.random = new Random();
  }


  public void run() {
    Console.writeLine("Published scheduled message...");
    scheduledExecutorService.scheduleAtFixedRate(this::scheduledMessageCallback, 0, 1, SECONDS);
  }

  private void scheduledMessageCallback() {
    try {
      UdpUtil.sendString(host, port, StringUtil.getRandomTextEN(random, 30));
    } catch (final Throwable e) {
      Console.Error.writeLine(e.getMessage());
    }
  }

  public static void main(String[] args) {
    final var sender = new Sender("localhost", 4444);
    sender.run();
  }
}
