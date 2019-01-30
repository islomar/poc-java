package es.islomar.bestpricefinder;

import static java.util.stream.Collectors.toList;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class BestPriceFinder {

  // We need more shops than cores in our CPU for seeing the effect of parallel streams
  private static final List<Shop> ALL_SHOPS =
      Arrays.asList(
          new Shop("BestPrices"),
          new Shop("LetsSaveBig"),
          new Shop("MyFavoriteShop"),
          new Shop("BuyItAll"),
          new Shop("BestPrices"),
          new Shop("LetsSaveBig"),
          new Shop("MyFavoriteShop"),
          new Shop("BestPrices"),
          new Shop("LetsSaveBig"),
          new Shop("MyFavoriteShop"),
          new Shop("BestPrices"),
          new Shop("LetsSaveBig"),
          new Shop("MyFavoriteShop"),
          new Shop("BestPrices"),
          new Shop("LetsSaveBig"),
          new Shop("MyFavoriteShop"));

  // using daemon threads does not prevent the termination of the program
  // no difference for the performance
  private static final Executor EXECUTOR =
      Executors.newFixedThreadPool(
          Math.min(ALL_SHOPS.size(), 100),
          r -> {
            Thread t = new Thread(r);
            t.setDaemon(true);
            return t;
          });
  private final ShopService shopService;

  public BestPriceFinder() {
    this.shopService = new ShopService();
  }

  public List<String> findPricesSequential(String product) {
    return ALL_SHOPS
        .stream()
        .map(
            shop ->
                String.format(
                    "%s price is %s", shop.getName(), this.shopService.getPrice(product, shop)))
        .collect(toList());
  }

  public List<String> findPricesWithParallel(String product) {
    return ALL_SHOPS
        .parallelStream()
        .map(
            shop ->
                String.format(
                    "%s price is %s", shop.getName(), this.shopService.getPrice(product, shop)))
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
                                "%s price is %s",
                                shop.getName(), this.shopService.getPrice(product, shop))))
            .collect(toList());

    // Wait for the completion of all asynchronous operations
    return priceFutures.stream().map(CompletableFuture::join).collect(toList());
  }

  public List<String> findPricesWithStreamsAndAsyncAndExecutor(String product) {
    // Calculate each price asynchronously with a CompletableFuture
    List<CompletableFuture<String>> priceFutures =
        ALL_SHOPS
            .stream()
            .map(
                shop ->
                    CompletableFuture.supplyAsync(
                        () ->
                            String.format(
                                "%s price is %s",
                                shop.getName(), this.shopService.getPrice(product, shop)),
                        EXECUTOR))
            .collect(toList());

    // Wait for the completion of all asynchronous operations
    return priceFutures.stream().map(CompletableFuture::join).collect(toList());
  }

  public List<String> syncFindPricesWithDiscounts(String product) {
    return ALL_SHOPS
        .stream()
        .map(shop -> this.shopService.getPrice(product, shop))
        .map(Quote::parse)
        .map(DiscountService::applyDiscount)
        .collect(toList());
  }

  public List<String> asyncFindPricesWithDiscounts(String product) {
    List<CompletableFuture<String>> priceFutures =
        ALL_SHOPS
            .stream()
            .map(
                shop ->
                    CompletableFuture.supplyAsync(
                        () -> this.shopService.getPrice(product, shop), EXECUTOR))
            .map(future -> future.thenApply(Quote::parse))
            .map(
                future ->
                    future.thenCompose(
                        quote ->
                            CompletableFuture.supplyAsync(
                                () -> DiscountService.applyDiscount(quote), EXECUTOR)))
            .collect(toList());

    return priceFutures.stream().map(CompletableFuture::join).collect(toList());
  }
}
