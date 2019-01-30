package es.islomar.bestpricefinder;

import static es.islomar.bestpricefinder.Util.delay;
import static es.islomar.bestpricefinder.Util.format;

public class DiscountService {

  public static String applyDiscount(Quote quote) {
    return quote.getShopName() + " price is " + apply(quote.getPrice(), quote.getDiscountCode());
  }

  private static double apply(double price, DiscountCode code) {
    delay();
    return format(price * (100 - code.getPercentage()) / 100);
  }
}
