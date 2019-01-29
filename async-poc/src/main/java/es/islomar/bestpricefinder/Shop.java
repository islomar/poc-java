package es.islomar.bestpricefinder;

import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public class Shop {

  private final String name;

  public Shop(String name) {
    this.name = name;
  }

  public double getPrice(String product) {
    return calcualtePrice(product);
  }

  public Future<Double> getPriceAsync(String product) {
    CompletableFuture<Double> futurePrice = new CompletableFuture<>();
    // fork a different thread that will perform the actual price calculation
    new Thread(
            () -> {
              double price = calcualtePrice(product);
              futurePrice.complete(price);
            })
        .start();
    return futurePrice;
  }

  private double calcualtePrice(String product) {
    delay();
    Random random = new Random();
    return random.nextDouble() * product.charAt(0) + product.charAt(1);
  }

  private static void delay() {
    try {
      Thread.sleep(1000L);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }
}
