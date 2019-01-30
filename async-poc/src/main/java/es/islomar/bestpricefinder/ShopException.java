package es.islomar.bestpricefinder;

public class ShopException extends RuntimeException {

  public ShopException(String errorMessage) {
    super(errorMessage);
  }

  public ShopException(Exception ex) {
    super(ex);
  }
}
