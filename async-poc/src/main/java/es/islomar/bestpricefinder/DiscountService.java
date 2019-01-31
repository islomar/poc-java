package es.islomar.bestpricefinder;

import static es.islomar.bestpricefinder.Util.delay;
import static es.islomar.bestpricefinder.Util.format;

import es.islomar.bestpricefinder.model.DiscountCode;
import es.islomar.bestpricefinder.model.Quote;

public class DiscountService {

  public static String applyDiscount(Quote quote, boolean isRandomDelay) {
    return quote.getShopName()
        + " price is "
        + apply(quote.getPrice(), quote.getDiscountCode(), isRandomDelay);
  }

  private static double apply(double price, DiscountCode code, boolean isRandomDelay) {
    delay(isRandomDelay);
    return format(price * (100 - code.getPercentage()) / 100);
  }
}
