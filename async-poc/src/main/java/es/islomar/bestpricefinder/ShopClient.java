package es.islomar.bestpricefinder;

import java.util.concurrent.Future;

public class ShopClient {

  public static void main(String[] args) {
    // Query the shop to retrieve the price of a product
    Shop shop = new Shop("BestShop");
    long start = System.nanoTime();
    Future<Double> futurePrice = shop.getPriceAsync("myPhone");
    long invocationTime = ((System.nanoTime() - start) / 1_000_000);
    System.out.println("Invocation returned after " + invocationTime + " msecs");

    // Read the price from the Future or block until it won't be available
    try {
      System.out.println("Price is " + futurePrice.get());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    long retrievalTime = ((System.nanoTime() - start) / 1_000_000);
    System.out.println("Price returned after " + retrievalTime + " msecs");
  }
}
