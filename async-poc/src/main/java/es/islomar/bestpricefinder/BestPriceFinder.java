package es.islomar.bestpricefinder;

import static java.util.stream.Collectors.toList;

import es.islomar.bestpricefinder.model.Money;
import es.islomar.bestpricefinder.model.Quote;
import es.islomar.bestpricefinder.model.Shop;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

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
  private final boolean isRandomDelay;

  public BestPriceFinder(boolean isRandomDelay) {
    this.shopService = new ShopService(isRandomDelay);
    this.isRandomDelay = isRandomDelay;
  }

  public List<String> findPricesSequential(String product) {
    return ALL_SHOPS.stream()
        .map(
            shop ->
                String.format(
                    "%s price is %s",
                    shop.getName(), this.shopService.getPriceWithDiscount(product, shop)))
        .collect(toList());
  }

  public List<String> findPricesWithParallel(String product) {
    return ALL_SHOPS
        .parallelStream()
        .map(
            shop ->
                String.format(
                    "%s price is %s",
                    shop.getName(), this.shopService.getPriceWithDiscount(product, shop)))
        .collect(toList());
  }

  public List<String> findPricesWithStreamsAndAsync(String product) {
    // Calculate each price asynchronously with a CompletableFuture
    List<CompletableFuture<String>> priceFutures =
        ALL_SHOPS.stream()
            .map(
                shop ->
                    CompletableFuture.supplyAsync(
                        () ->
                            String.format(
                                "%s price is %s",
                                shop.getName(),
                                this.shopService.getPriceWithDiscount(product, shop))))
            .collect(toList());

    // Wait for the completion of all asynchronous operations
    return priceFutures.stream().map(CompletableFuture::join).collect(toList());
  }

  public List<String> findPricesWithStreamsAndAsyncAndExecutor(String product) {
    // Calculate each price asynchronously with a CompletableFuture
    List<CompletableFuture<String>> priceFutures =
        ALL_SHOPS.stream()
            .map(
                shop ->
                    CompletableFuture.supplyAsync(
                        () ->
                            String.format(
                                "%s price is %s",
                                shop.getName(),
                                this.shopService.getPriceWithDiscount(product, shop)),
                        EXECUTOR))
            .collect(toList());

    // Wait for the completion of all asynchronous operations
    return priceFutures.stream().map(CompletableFuture::join).collect(toList());
  }

  public List<String> syncFindPricesWithDiscounts(String product) {
    return ALL_SHOPS.stream()
        .map(shop -> this.shopService.getPriceWithDiscount(product, shop))
        .map(Quote::parse)
        .map(quote -> DiscountService.applyDiscount(quote, this.isRandomDelay))
        .collect(toList());
  }

  /**
   * thenApply() does not block the code until the CompletableFuture on which we''e invoking it is
   * completed
   */
  public List<String> asyncFindPricesWithDiscounts(String product) {
    List<CompletableFuture<String>> priceFutures =
        this.findPricesWithDiscountStream(product).collect(toList());

    return priceFutures.stream().map(CompletableFuture::join).collect(toList());
  }

  // Combine two independent tasks
  public List<Double> futurePriceInUSD(String product) {
    Stream<CompletableFuture<Double>> futurePricesInUSD =
        ALL_SHOPS.stream()
            .map(
                shop ->
                    CompletableFuture.supplyAsync(() -> this.shopService.getPrice(product))
                        .thenCombine(
                            CompletableFuture.supplyAsync(
                                () ->
                                    ExchangeService.getRate(
                                        Money.EUR, Money.USD, this.isRandomDelay)),
                            (price, rate) -> price * rate));

    return futurePricesInUSD.map(CompletableFuture::join).collect(toList());
  }

  // Register an action to each CompletableFuture; this action consumes the value of the
  // CompletableFuture as soon as it completes
  public void asyncFindPricesAsap(String product) {
    CompletableFuture[] futures =
        findPricesWithDiscountStream(product)
            // it prints the results as soon as they are ready
            .map(f -> f.thenAccept(System.out::println))
            .toArray(size -> new CompletableFuture[size]);
    CompletableFuture.allOf(futures).join(); // waits for the slowest action to complete
  }

  public void asyncFindPricesAndPrintThemIncrementally(String product) {
    long start = System.nanoTime();
    CompletableFuture[] futures =
        findPricesWithDiscountStream(product)
            .map(
                f ->
                    f.thenAccept(
                        s ->
                            System.out.println(
                                s
                                    + " (done in "
                                    + ((System.nanoTime() - start) / 1_000_000)
                                    + " msecs)")))
            .toArray(size -> new CompletableFuture[size]);
    CompletableFuture.allOf(futures).join();
    System.out.println(
        "All shops have now responded in " + ((System.nanoTime() - start) / 1_000_000) + " msecs");
  }

  private Stream<CompletableFuture<String>> findPricesWithDiscountStream(String product) {
    return ALL_SHOPS.stream()
        .map(
            shop ->
                CompletableFuture.supplyAsync(
                    () -> this.shopService.getPriceWithDiscount(product, shop), EXECUTOR))
        .map(future -> future.thenApply(Quote::parse))
        .map(
            future ->
                future.thenCompose(
                    quote ->
                        CompletableFuture.supplyAsync(
                            () -> DiscountService.applyDiscount(quote, false), EXECUTOR)));
  }
}
