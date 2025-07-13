package org.csystem.udp.broadcast;



import com.karandev.io.util.console.Console;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

public class Receiver {
  private final String host;
  private final int port;

  public Receiver(String host, int port) {
    this.host = host;
    this.port = port;
  }


  private void run() {
    final var buf = new byte["Merhaba ben Nuri Can!".length()];

    try (var datagramSocket = new DatagramSocket(port, InetAddress.getByName(host))) {
      var datagramPacket = new DatagramPacket(buf, buf.length);

      datagramSocket.receive(datagramPacket);

      var str = new String(datagramPacket.getData(), 0, datagramPacket.getLength(), StandardCharsets.UTF_8);

      Console.writeLine(str);
    }
    catch (IOException ex) {
      Console.Error.writeLine("IO Problem occurred:%s", ex.getMessage());
    }
  }

  public static void main(String[] args) {
    final var receiver = new Receiver("localhost", 4444);
    while(true) {
      receiver.run();
    }
  }
}
