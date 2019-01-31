package es.islomar.bestpricefinder;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Random;

public class Util {

  private static final DecimalFormat formatter =
      new DecimalFormat("#.##", new DecimalFormatSymbols(Locale.US));

  public static void delay() {
    try {
      Thread.sleep(1000L);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  public static void randomDelay() {
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
