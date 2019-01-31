package es.islomar.async;

import static es.islomar.bestpricefinder.Util.delay;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.hamcrest.core.IsInstanceOf;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Things to understand: CompletionStage, thenApply(), thenCompose(), exceptionally(),
 * supplyAsync(), best practices for exception handling
 */
public class MyAsyncPoCShould {

  private final MyCollaborator myCollaborator = mock(MyCollaborator.class);
  private MyAsyncPoC myAsyncPoC;

  @BeforeEach
  public void setUp() {
    this.myAsyncPoC = new MyAsyncPoC(this.myCollaborator);
  }

  @Test
  public void return_string_when_complete_completablefuture_is_called()
      throws ExecutionException, InterruptedException {
    Future<String> stringFuture = this.myAsyncPoC.exampleWithCompletableFuture("Hello");
    String result = stringFuture.get();

    // When calling get() we block and wait for the answer
    // get() might throw ExecutionException or InterruptedException
    assertThat(result, is("Hello"));
  }

  @Test
  public void return_string_when_complete_completablefuture_is_called_within_another_tread()
      throws Exception {
    Future<String> stringFuture =
        this.myAsyncPoC.exampleWithCompletableFutureInAnotherThread("Hello");
    String result = stringFuture.get();

    assertThat(result, is("Hello"));
  }

  @Test
  public void throw_CancellationException_when_cancelling_a_future() {
    Future<String> stringFuture = this.myAsyncPoC.calculateAsyncWithCancellation();

    assertThrows(CancellationException.class, stringFuture::get);
  }

  @Test
  public void create_compleatefuture_out_of_supplier()
      throws ExecutionException, InterruptedException {
    // supplyAsync() create a CompletableFuture instance out of Supplier functional type
    CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> "Hello");

    String result = future.get();

    assertThat(result, is("Hello"));
  }

  @Test
  public void create_compleatefuture_out_of_runnable() {
    doNothing().when(this.myCollaborator).doStuff();
    // runAsync() create a CompletableFuture instance out of Runnable functional type
    CompletableFuture<Void> future = CompletableFuture.runAsync(() -> this.myAsyncPoC.execute());

    // Probably because it is very fast, the test passes both calling or not get()
    // future.get();

    verify(this.myCollaborator).doStuff();
  }

  @Test
  public void pre_java8_future() {
    int timeoutInMilliseconds = 1000;
    ExecutorService executor = Executors.newSingleThreadExecutor();
    Future<String> future = executor.submit(() -> doSomeLongComputation(timeoutInMilliseconds * 2));

    doSomethingElseWhileTheAsyncOperationIsProgressing();

    assertThrows(
        TimeoutException.class, () -> future.get(timeoutInMilliseconds, TimeUnit.MILLISECONDS));
  }

  @Test
  // thenApply() returns a future with the result
  public void example_with_thenApply() throws ExecutionException, InterruptedException {
    CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> "Hello");

    CompletableFuture<String> future = completableFuture.thenApply(s -> s + " World");

    assertEquals("Hello World", future.get());
  }

  @Test
  // thenAccept() returns a future with void (no result)
  public void example_with_thenAccept() throws ExecutionException, InterruptedException {
    CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> "Hello");

    CompletableFuture<Void> future =
        completableFuture.thenAccept(s -> System.out.println("Computation returned: " + s));

    future.get();
  }

  @Test
  // if you neither need the value of the computation nor want to return some value at the end of
  // the chain, then you can pass a Runnable lambda to the thenRun method. I
  public void example_with_thenRun() throws ExecutionException, InterruptedException {
    CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> "Hello");

    CompletableFuture<Void> future =
        completableFuture.thenRun(() -> System.out.println("Computation finished."));

    future.get();
  }

  @Test
  // thenAcceptBoth(): When you want to do something with two Futures‘ results, but don’t need to
  // pass any resulting value down a Future chain.
  public void example_with_thenAcceptBoth() {
    CompletableFuture.supplyAsync(() -> "Hello")
        .thenAcceptBoth(
            CompletableFuture.supplyAsync(() -> " World"), (s1, s2) -> System.out.println(s1 + s2));
  }

  @Test
  public void example_for_handling_exceptions() throws ExecutionException, InterruptedException {
    String name = null;

    // handle() provides a default value for the name in case an exception happened
    CompletableFuture<String> completableFuture =
        CompletableFuture.supplyAsync(
                () -> {
                  if (name == null) {
                    throw new RuntimeException("Computation error!");
                  }
                  return "Hello, " + name;
                })
            .handle((s, t) -> s != null ? s : "Hello, Stranger!");

    assertEquals("Hello, Stranger!", completableFuture.get());
  }

  @Test
  public void example_with_completeExceptionally() {
    CompletableFuture<String> completableFuture = new CompletableFuture<>();

    completableFuture.completeExceptionally(new RuntimeException("Calculation failed!"));

    assertThrows(ExecutionException.class, () -> completableFuture.get()); // ExecutionException
  }

  @Test
  public void example_with_defaultExecutor() {
    Executor executor = new CompletableFuture().defaultExecutor();

    System.out.println("Default executor: " + executor.toString());
    assertThat(executor, IsInstanceOf.instanceOf(Executor.class));
  }

  @Test
  public void example_with_orTimeout() {
    CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> this.slowComputation());

    ExecutionException executionException =
        assertThrows(
            ExecutionException.class, () -> future.orTimeout(10, TimeUnit.MILLISECONDS).get());

    assertThat(executionException.getCause(), IsInstanceOf.instanceOf(TimeoutException.class));
  }

  @Test
  public void example_with_completeOnTimeout() throws ExecutionException, InterruptedException {
    CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> this.slowComputation());

    String response =
        future.completeOnTimeout("Default timeout value", 10, TimeUnit.MILLISECONDS).get();

    assertThat(response, is("Default timeout value"));
  }

  @Test
  public void example_with_failedFuture() {
    CompletableFuture<Object> future = CompletableFuture.failedFuture(new RuntimeException());

    assertTrue(future.isDone());
    assertTrue(future.isCompletedExceptionally());
    assertFalse(future.isCancelled());
  }

  @Test
  public void example_with_exceptionally() throws ExecutionException, InterruptedException {
    Integer age = -1;

    CompletableFuture<String> maturityFuture =
        CompletableFuture.supplyAsync(
                () -> {
                  if (age < 0) {
                    throw new IllegalArgumentException("Age can not be negative");
                  }
                  if (age > 18) {
                    return "Adult";
                  } else {
                    return "Child";
                  }
                })
            .exceptionally(
                ex -> {
                  System.out.println("Oops! We have an exception - " + ex.getMessage());
                  return "Unknown!";
                });

    assertThat(maturityFuture.get(), is("Unknown!"));
    assertTrue(maturityFuture.isDone());
    assertFalse(maturityFuture.isCompletedExceptionally());
    assertFalse(maturityFuture.isCancelled());
  }

  private String slowComputation() {
    delay(false);
    return "The right response";
  }

  private void doSomethingElseWhileTheAsyncOperationIsProgressing() {
    System.out.println("We finished!!");
  }

  private String doSomeLongComputation(int timeoutInMilliseconds) throws InterruptedException {
    // Sleeping longer than the timeout configured in the Future will throw a TimeoutException
    Thread.sleep(timeoutInMilliseconds);
    return "Hello world";
  }
}
