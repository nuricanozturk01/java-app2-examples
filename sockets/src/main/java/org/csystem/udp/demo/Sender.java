package org.csystem.udp.demo;

import com.karandev.util.console.Console;
import org.csystem.util.string.StringUtil;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
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
    try (final var datagramSocket = new DatagramSocket()) {
      final var message = StringUtil.getRandomTextEN(random, 30).getBytes(StandardCharsets.UTF_8);
      final var datagramPacket = new DatagramPacket(message, message.length, InetAddress.getByName(host), port);

      datagramSocket.send(datagramPacket);
    } catch (final IOException e) {
      Console.Error.writeLine(e.getMessage());
    }
  }

  public static void main(String[] args) {
    final var sender = new Sender("localhost", 4444);
    sender.run();
  }
}
