package es.islomar.bestpricefinder;

public class Quote {

  private final String shopName;
  private final double price;
  private final DiscountService.Code discountCode;

  public Quote(String shopName, double price, DiscountService.Code discountCode) {
    this.shopName = shopName;
    this.price = price;
    this.discountCode = discountCode;
  }

  public static Quote parse(String shopPriceDiscount) {
    String[] split = shopPriceDiscount.split(":");
    String shopName = split[0];
    double price = Double.parseDouble(split[1]);
    DiscountService.Code discountCode = DiscountService.Code.valueOf(split[2]);
    return new Quote(shopName, price, discountCode);
  }

  public String getShopName() {
    return this.shopName;
  }

  public double getPrice() {
    return this.price;
  }

  public DiscountService.Code getDiscountCode() {
    return this.discountCode;
  }
}
