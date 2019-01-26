package es.islomar.quizzes;

public class ToString {

  static int number = 10;

  public static void main(String... doYourBest) {
    new ToString();
  }

  public ToString() {
    System.out.println(this);
  }

  public String toString() { return "ToString.number = " + number; }

  static class MisterBean extends ToString {}
}
