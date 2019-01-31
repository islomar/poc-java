package es.islomar.bestpricefinder;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class BestPriceFinderTest {

  public static final String ANY_PRODUCT = "myPhone";
  private BestPriceFinder bestPriceFinder;

  @BeforeEach
  public void setUp() {
    this.bestPriceFinder = new BestPriceFinder();
  }

  @Test
  // Running with regular streams will take n times the calculation of one product
  // This is a very slow test to run
  public void run_action_in_synchronous_way() {
    long start = System.nanoTime();

    this.bestPriceFinder.findPricesSequential(ANY_PRODUCT);

    long totalTimeElapsed = ((System.nanoTime() - start) / 1_000_000);
    System.out.println(String.format("Time elapsed: %s msecs", totalTimeElapsed));
  }

  @Test
  // Running with parallel streams will only take the time of one calculation
  // Since we are using more shops than cores (16 shops vs 12 cores)
  public void run_action_in_synchronous_way_with_parallel_streams() {
    long start = System.nanoTime();

    this.bestPriceFinder.findPricesWithParallel(ANY_PRODUCT);

    long totalTimeElapsed = ((System.nanoTime() - start) / 1_000_000);
    System.out.println(String.format("Time elapsed: %s msecs", totalTimeElapsed));
  }

  @Test
  // Running with parallel streams will only take the time of two calculations,
  // Since we are using more shops than cores (16 shops vs 12 cores)
  public void run_action_with_async_streams() {
    long start = System.nanoTime();

    this.bestPriceFinder.findPricesWithStreamsAndAsync(ANY_PRODUCT);

    long totalTimeElapsed = ((System.nanoTime() - start) / 1_000_000);
    System.out.println(String.format("Time elapsed: %s msecs", totalTimeElapsed));
  }

  @Test
  // Running with parallel streams and an Executor with many threads, it will take again the time
  // of one calculation (there are more threads than shops)
  public void run_action_with_async_streams_and_executor() {
    long start = System.nanoTime();

    this.bestPriceFinder.findPricesWithStreamsAndAsyncAndExecutor(ANY_PRODUCT);

    long totalTimeElapsed = ((System.nanoTime() - start) / 1_000_000);
    System.out.println(String.format("Time elapsed: %s msecs", totalTimeElapsed));
  }

  @Test
  // This implementation is very slow: it calls first one service (for each shop) and then another
  // service
  public void synchronously_find_prices_with_discounts_in_a_pipeline_way() {
    long start = System.nanoTime();

    this.bestPriceFinder.syncFindPricesWithDiscounts(ANY_PRODUCT);

    long totalTimeElapsed = ((System.nanoTime() - start) / 1_000_000);
    System.out.println(String.format("Time elapsed: %s msecs", totalTimeElapsed));
  }

  @Test
  public void asynchronously_find_prices() {
    long start = System.nanoTime();

    this.bestPriceFinder.asyncFindPricesWithDiscounts(ANY_PRODUCT);

    long totalTimeElapsed = ((System.nanoTime() - start) / 1_000_000);
    System.out.println(String.format("Time elapsed: %s msecs", totalTimeElapsed));
  }

  @Test
  public void combine_results_from_several_services() {
    long start = System.nanoTime();

    this.bestPriceFinder.futurePriceInUSD(ANY_PRODUCT);

    long totalTimeElapsed = ((System.nanoTime() - start) / 1_000_000);
    System.out.println(String.format("Time elapsed: %s msecs", totalTimeElapsed));
  }

  @Test
  public void process_found_prices_asap_and_wait_for_all_of_them_at_the_end() {
    long start = System.nanoTime();

    this.bestPriceFinder.asyncFindPricesAsap(ANY_PRODUCT);

    long totalTimeElapsed = ((System.nanoTime() - start) / 1_000_000);
    System.out.println(String.format("Time elapsed: %s msecs", totalTimeElapsed));
  }
}
