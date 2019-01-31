package es.islomar.bestpricefinder.model;

import java.util.Random;

public class Shop {

  private final String name;
  private final Random random;

  public Shop(String name) {
    this.name = name;
    this.random = new Random(name.charAt(0) * name.charAt(1) * name.charAt(2));
  }

  public String getName() {
    return this.name;
  }

  public String getDiscountCode() {
    return DiscountCode.values()[this.random.nextInt(DiscountCode.values().length)].name();
  }
}
