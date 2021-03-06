package es.islomar.bestpricefinder;

import static es.islomar.bestpricefinder.Util.delay;

import es.islomar.bestpricefinder.model.Shop;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public class ShopService {

  private final boolean isRandomDelay;
  private boolean shouldCancel;
  private boolean shouldThrowShopException;
  private boolean shouldThrowRuntimeException;

  public ShopService() {
    this(false);
  }

  public ShopService(boolean isRandomDelay) {
    this.isRandomDelay = isRandomDelay;
  }

  public String getPriceWithDiscount(String product, Shop shop) {
    double price = calculatePrice(product);
    return shop.getName() + ":" + price + ":" + shop.getDiscountCode();
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
                if (this.shouldCancel) {
                  futurePrice.cancel(true);
                  return;
                }
                double price = calculatePrice(product);
                futurePrice.complete(price);
              } catch (Exception ex) {
                // It makes no sense to manually throw an exception here: completeExceptionally()
                // does it (ExecutionException)
                futurePrice.completeExceptionally(ex);
              }
            })
        .start();
    return futurePrice;
  }

  public Future<Double> getPriceWithSupplyAsync(String product) {
    CompletableFuture<Double> futurePrice =
        CompletableFuture.supplyAsync(() -> calculatePrice(product));
    if (this.shouldCancel) {
      futurePrice.cancel(true);
    }
    return futurePrice;
  }

  private double calculatePrice(String product) {
    if (this.shouldThrowShopException) {
      throw new ShopException("Something bad happened!");
    }
    if (this.shouldThrowRuntimeException) {
      throw new RuntimeException("Unexpected error!");
    }
    delay(this.isRandomDelay);
    System.out.println("Calculated price for " + product);
    Random random = new Random();
    return random.nextDouble() * product.charAt(0) + product.charAt(1);
  }
}
