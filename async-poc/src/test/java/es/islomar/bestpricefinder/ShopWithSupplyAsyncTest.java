package es.islomar.bestpricefinder;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.junit.jupiter.api.Test;

public class ShopWithSupplyAsyncTest {

  @Test
  public void when_action_gets_completed_then_future_is_done()
      throws ExecutionException, InterruptedException {
    // Query the shop to retrieve the price of a product
    Shop shop = new Shop("BestShop");
    long start = System.nanoTime();
    Future<Double> futurePrice = shop.getPriceWithSupplyAsync("myPhone");
    logInvocationTime(start, "Invocation returned after ");

    // Read the price from the Future or block until it won't be available
    System.out.println("Price is " + futurePrice.get());

    logInvocationTime(start, "Price returned after ");
    assertTrue(futurePrice.isDone());
    assertFalse(futurePrice.isCancelled());
  }

  @Test
  public void
      when_action_completes_exceptionally_then_ExecutionException_is_thrown_and_future_is_done_but_not_cancelled() {
    Shop shop = new Shop("BestShop");
    shop.shouldThrowShopException();
    Future<Double> futurePrice = shop.getPriceWithSupplyAsync("myPhone");

    ExecutionException executionException =
        assertThrows(ExecutionException.class, () -> futurePrice.get());

    assertThat(executionException.getCause(), instanceOf(ShopException.class));
    assertTrue(futurePrice.isDone());
    assertFalse(futurePrice.isCancelled());
  }

  @Test
  public void
      when_action_gets_cancelled_then_CancellationException_is_thrown_and_future_is_done_and_cancelled() {
    Shop shop = new Shop("BestShop");
    shop.shouldCancel();
    Future<Double> futurePrice = shop.getPriceWithSupplyAsync("myPhone");

    assertThrows(CancellationException.class, () -> futurePrice.get());

    assertTrue(futurePrice.isDone());
    assertTrue(futurePrice.isCancelled());
  }

  @Test
  public void when_action_throws_RuntimeException_then_ExecutionException_is_thrown() {
    Shop shop = new Shop("BestShop");
    shop.shouldThrowRuntimeException();
    Future<Double> futurePrice = shop.getPriceWithSupplyAsync("myPhone");

    ExecutionException executionException =
        assertThrows(ExecutionException.class, () -> futurePrice.get());

    assertThat(executionException.getCause(), instanceOf(RuntimeException.class));
    assertTrue(futurePrice.isDone());
    assertFalse(futurePrice.isCancelled());
  }

  private void logInvocationTime(long start, String s) {
    long invocationTime = ((System.nanoTime() - start) / 1_000_000);
    System.out.println(s + invocationTime + " msecs");
  }
}
