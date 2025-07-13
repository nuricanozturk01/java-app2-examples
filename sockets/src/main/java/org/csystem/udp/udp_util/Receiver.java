package org.csystem.udp.udp_util;

import com.karandev.io.util.console.Console;
import com.karandev.util.net.UdpUtil;
import com.karandev.util.net.exception.NetworkException;

import java.net.DatagramSocket;
import java.net.SocketException;

public class Receiver {
  private final int port;

  public Receiver(int port) {
    this.port = port;
  }


  public void run() {
    try (var datagramSocket = new DatagramSocket(port)) {
      while (true)
        receive(datagramSocket);
    } catch (SocketException e) {
      throw new RuntimeException(e);
    }
  }
  private void receive(final DatagramSocket datagramSocket) {
    try {
      var str = UdpUtil.receiveString(datagramSocket, 1024);

      Console.writeLine(str);
    }
    catch (NetworkException ex) {
      Console.Error.writeLine("IO Problem occurred:%s", ex.getMessage());
    }
  }

  public static void main(String[] args) {
    final var receiver = new Receiver(4444);
    receiver.run();
  }
}
