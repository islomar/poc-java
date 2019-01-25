package es.islomar.async;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MyClass {

  public Future<String> calculateAsync(String message) {
    CompletableFuture<String> completableFuture = new CompletableFuture<>();

    executeFutureInAnotherThread(message, completableFuture);

    return completableFuture;
  }

  private void executeFutureInAnotherThread(final String message,
                                            final CompletableFuture<String> completableFuture) {
    Executors.newCachedThreadPool().submit(() -> {
      Thread.sleep(500);
      completableFuture.complete(message);
      return null;
    });
  }

}