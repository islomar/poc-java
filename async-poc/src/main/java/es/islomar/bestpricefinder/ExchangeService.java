package es.islomar.bestpricefinder;

import static es.islomar.bestpricefinder.Util.delay;

import es.islomar.bestpricefinder.model.Money;

public class ExchangeService {

  public static double getRate(Money source, Money destination, boolean isRandomDelay) {
    delay(isRandomDelay);
    return destination.getRate() / source.getRate();
  }
}
