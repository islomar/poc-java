package es.islomar.async;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Things to understand:
 *      * CompletionStage
 *      * thenApply()
 *      * thenCompose()
 *      * exceptionally()
 *      * supplyAsync()
 *      * Best practices for exception handling
 */


public class MyAsyncPoCShould {

  MyCollaborator myCollaborator = mock(MyCollaborator.class);
  private MyAsyncPoC myAsyncPoC;


  @BeforeEach
    public void setUp() {
      this.myAsyncPoC = new MyAsyncPoC(myCollaborator);
    }

    @Test
    public void return_string_when_complete_completablefuture_is_called() throws ExecutionException, InterruptedException {
        Future<String> stringFuture = this.myAsyncPoC.exampleWithCompletableFuture("Hello");
        String result = stringFuture.get();

        // When calling get() we block and wait for the answer
        // get() might throw ExecutionException or InterruptedException
        assertThat(result, is("Hello"));
    }

    @Test
    public void return_string_when_complete_completablefuture_is_called_within_another_tread() throws Exception {
        Future<String> stringFuture = this.myAsyncPoC.exampleWithCompletableFutureInAnotherThread("Hello");
        String result = stringFuture.get();

        assertThat(result, is("Hello"));
    }

    @Test
    public void throw_CancellationException_when_cancelling_a_future() {
        Future<String> stringFuture = this.myAsyncPoC.calculateAsyncWithCancellation();

        assertThrows(CancellationException.class, () -> stringFuture.get());
    }

  @Test
  public void create_compleatefuture_out_of_supplier() throws ExecutionException,
                                                           InterruptedException {
        // supplyAsync() create a CompletableFuture instance out of Supplier functional type
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> "Hello");

        final String result = future.get();

        assertThat(result, is("Hello"));
    }

  @Test
  public void create_compleatefuture_out_of_runnable() throws ExecutionException,
                                                              InterruptedException {
    doNothing().when(myCollaborator).doStuff();
    // runAsync() create a CompletableFuture instance out of Runnable functional type
    CompletableFuture<Void> future = CompletableFuture.runAsync(() -> this.myAsyncPoC.execute());

    // Probably because it is very fast, the test passes both calling or not get()
    // future.get();

    verify(myCollaborator).doStuff();
  }
}