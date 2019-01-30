package es.islomar.bestpricefinder;

import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public class Shop {

  private final String name;
  private boolean shouldCancel;
  private boolean shouldThrowShopException;
  private boolean shouldThrowRuntimeException;

  public Shop(String name) {
    this.name = name;
  }

  public double getPrice(String product) {
    return calculatePrice(product);
  }

  public void shouldThrowShopException() {
    this.shouldThrowShopException = true;
  }

  public void shouldThrowRuntimeException() {
    this.shouldThrowRuntimeException = true;
  }

  public void shouldCancel() {
    this.shouldCancel = true;
  }

  public Future<Double> getPriceAsync(String product) {
    CompletableFuture<Double> futurePrice = new CompletableFuture<>();
    // fork a different thread that will perform the actual price calculation
    new Thread(
            () -> {
              try {
                if (this.shouldThrowShopException) {
                  throw new ShopException("Something bad happened!");
                }
                if (this.shouldThrowRuntimeException) {
                  throw new RuntimeException("Unexpected error!");
                }
                if (this.shouldCancel) {
                  futurePrice.cancel(true);
                  return;
                }
                double price = calculatePrice(product);
                futurePrice.complete(price);
              } catch (ShopException ex) {
                futurePrice.completeExceptionally(ex);
              } catch (Exception ex) {
                // It makes no sense to manually throw an exception here: completeExceptionally()
                // does it (ExecutionException)
                futurePrice.completeExceptionally(ex);
              }
            })
        .start();
    return futurePrice;
  }

  private double calculatePrice(String product) {
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
