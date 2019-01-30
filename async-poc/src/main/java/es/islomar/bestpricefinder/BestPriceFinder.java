package es.islomar.bestpricefinder;

import static java.util.stream.Collectors.toList;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class BestPriceFinder {

  // We need more shops than cores in our CPU for seeing the effect of parallel streams
  private static final List<ShopService> ALL_SHOPS =
      Arrays.asList(
          new ShopService("BestPrices"),
          new ShopService("LetsSaveBig"),
          new ShopService("MyFavoriteShop"),
          new ShopService("BuyItAll"),
          new ShopService("BestPrices"),
          new ShopService("LetsSaveBig"),
          new ShopService("MyFavoriteShop"),
          new ShopService("BestPrices"),
          new ShopService("LetsSaveBig"),
          new ShopService("MyFavoriteShop"),
          new ShopService("BestPrices"),
          new ShopService("LetsSaveBig"),
          new ShopService("MyFavoriteShop"),
          new ShopService("BestPrices"),
          new ShopService("LetsSaveBig"),
          new ShopService("MyFavoriteShop"));

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

  public List<String> findPrices(String product) {
    return ALL_SHOPS
        .stream()
        .map(
            shopService ->
                String.format(
                    "%s price is %s", shopService.getShopName(), shopService.getPrice(product)))
        .collect(toList());
  }

  public List<String> findPricesWithParallelStreams(String product) {
    return ALL_SHOPS
        .parallelStream()
        .map(
            shopService ->
                String.format(
                    "%s price is %s", shopService.getShopName(), shopService.getPrice(product)))
        .collect(toList());
  }

  public List<String> findPricesWithStreamsAndAsync(String product) {
    // Calculate each price asynchronously with a CompletableFuture
    List<CompletableFuture<String>> priceFutures =
        ALL_SHOPS
            .stream()
            .map(
                shopService ->
                    CompletableFuture.supplyAsync(
                        () ->
                            String.format(
                                "%s price is %s",
                                shopService.getShopName(), shopService.getPrice(product))))
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
                shopService ->
                    CompletableFuture.supplyAsync(
                        () ->
                            String.format(
                                "%s price is %s",
                                shopService.getShopName(), shopService.getPrice(product)),
                        EXECUTOR))
            .collect(toList());

    // Wait for the completion of all asynchronous operations
    return priceFutures.stream().map(CompletableFuture::join).collect(toList());
  }

  public List<String> findPricesWithDiscounts(String product) {
    return ALL_SHOPS
        .stream()
        .map(shopService -> shopService.getPrice(product))
        .map(Quote::parse)
        .map(DiscountService::applyDiscount)
        .collect(toList());
  }
}
