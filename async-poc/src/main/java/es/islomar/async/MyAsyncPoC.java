package es.islomar.async;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MyAsyncPoC {

  public Future<String> exampleWithCompletableFuture(String message) {
    CompletableFuture<String> completableFuture = new CompletableFuture<>();

    completableFuture.complete(message);

    return completableFuture;
  }

  public Future<String> exampleWithCompletableFutureInAnotherThread(String message) {
    CompletableFuture<String> completableFuture = new CompletableFuture<>();

    executeFutureInAnotherThread(message, completableFuture);

    return completableFuture;
  }

  public Future<String> calculateAsyncWithCancellation() {
    CompletableFuture<String> completableFuture = new CompletableFuture<>();

    Executors.newCachedThreadPool().submit(() -> {
      Thread.sleep(500);
      completableFuture.cancel(false);
      return null;
    });

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