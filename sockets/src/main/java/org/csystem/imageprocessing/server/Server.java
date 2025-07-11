package org.csystem.imageprocessing.server;

import com.karandev.util.console.Console;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
  private final ExecutorService threadPool;
  private final int port;
  private final int backlog;
  private final ServerSocket serverSocket;

  public Server(final int port, final int backlog) throws IOException {
    threadPool = Executors.newCachedThreadPool();
    this.port = port;
    this.backlog = backlog;
    serverSocket = new ServerSocket(this.port, this.backlog);
  }

  private void serverThreadCallback() {
    try {
      while (true) {
        Console.writeLine("Image Processing Server listening on port %d", this.port);
        final var socket = serverSocket.accept();
        // Bu nokta da client'ım var
        // Eğer burada thread yaratmadan devam edersem iteratif impl olur
        // Diğer gelen client mevcut client'ın işi bitene kadar bekler
        // Biz multithread yapacağız

        threadPool.execute(() -> this.handleClient(socket));
      }
    } catch (final IOException e) {
      Console.Error.writeLine();
    } catch (final Throwable e) {
      Console.Error.writeLine("");
    } finally {
      threadPool.shutdown();
    }
  }

  private void handleClient(final Socket socket) {
    try (socket) {
      Console.writeLine("Client connected via %s:%d", socket.getInetAddress().getHostAddress(), socket.getPort());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void run() {
    threadPool.execute(this::serverThreadCallback);
  }

  public static void main(String[] args) throws IOException {
    final var server = new Server(2121, 1000);
    server.run();
  }
}
