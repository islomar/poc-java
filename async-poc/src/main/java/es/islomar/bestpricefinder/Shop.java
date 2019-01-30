package es.islomar.bestpricefinder;

import static java.util.stream.Collectors.toList;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public class Shop {

  private final String name;
  private boolean shouldCancel;
  private boolean shouldThrowShopException;
  private boolean shouldThrowRuntimeException;
  private static final List<Shop> ALL_SHOPS =
      Arrays.asList(
          new Shop("BestPrices"),
          new Shop("LetsSaveBig"),
          new Shop("MyFavoriteShop"),
          new Shop("BuyItAll"));

  public Shop(String name) {
    this.name = name;
  }

  private String getName() {
    return this.name;
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

  public List<String> findPrices(String product) {
    return ALL_SHOPS
        .stream()
        .map(shop -> String.format("%s price is %.2f", shop.getName(), shop.getPrice(product)))
        .collect(toList());
  }

  public List<String> findPricesWithParallelStreams(String product) {
    return ALL_SHOPS
        .parallelStream()
        .map(shop -> String.format("%s price is %.2f", shop.getName(), shop.getPrice(product)))
        .collect(toList());
  }

  public List<String> findPricesWithStreamsAndAsync(String product) {
    // Calculate each price asynchronously with a CompletableFuture
    List<CompletableFuture<String>> priceFutures =
        ALL_SHOPS
            .stream()
            .map(
                shop ->
                    CompletableFuture.supplyAsync(
                        () ->
                            String.format(
                                "%s price is %.2f", shop.getName(), shop.getPrice(product))))
            .collect(toList());

    // Wait for the completion of all asynchronous operations
    return priceFutures.stream().map(CompletableFuture::join).collect(toList());
  }

  private double calculatePrice(String product) {
    if (this.shouldThrowShopException) {
      throw new ShopException("Something bad happened!");
    }
    if (this.shouldThrowRuntimeException) {
      throw new RuntimeException("Unexpected error!");
    }
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
