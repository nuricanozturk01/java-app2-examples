package org.csystem.imageprocessing.Client;

import java.io.IOException;
import java.net.Socket;

public class Client {

  private final String host;
  private final int port;

  public Client(String host, int port) {
    this.host = host;
    this.port = port;
  }


  public void run() throws IOException {
    try (final var socket = new Socket(this.host, this.port)) {

    }
  }

  public static void main(String[] args) throws IOException {
    final var client = new Client("localhost", 2121);
    client.run();
  }
}
