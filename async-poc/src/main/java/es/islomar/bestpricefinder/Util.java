package es.islomar.bestpricefinder;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Random;

public class Util {

  private static final DecimalFormat formatter =
      new DecimalFormat("#.##", new DecimalFormatSymbols(Locale.US));

  public static void delay(boolean isRandomDelay) {
    if (isRandomDelay) {
      randomDelay();
    } else {
      fixedDelay();
    }
  }

  private static void fixedDelay() {
    try {
      Thread.sleep(1000L);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  private static void randomDelay() {
    int delay = 500 + new Random().nextInt(200);
    try {
      Thread.sleep(delay);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  public static double format(double number) {
    synchronized (formatter) {
      return Double.parseDouble(formatter.format(number));
    }
  }
}
