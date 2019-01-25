package es.islomar.async;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
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

    @Test
    public void return_string_when_complete_completablefuture_is_called() throws ExecutionException, InterruptedException {
        MyAsyncPoC myAsyncPoC = new MyAsyncPoC();

        Future<String> stringFuture = myAsyncPoC.exampleWithCompletableFuture("Hello");
        String result = stringFuture.get();

        // When calling get() we block and wait for the answer
        // get() might throw ExecutionException or InterruptedException
        assertThat(result, is("Hello"));
    }

    @Test
    public void return_string_when_complete_completablefuture_is_called_within_another_tread() throws Exception {
        MyAsyncPoC myAsyncPoC = new MyAsyncPoC();

        Future<String> stringFuture = myAsyncPoC.exampleWithCompletableFutureInAnotherThread("Hello");
        String result = stringFuture.get();

        assertThat(result, is("Hello"));
    }

    @Test
    public void throw_CancellationException_when_cancelling_a_future() {
        MyAsyncPoC myAsyncPoC = new MyAsyncPoC();

        Future<String> stringFuture = myAsyncPoC.calculateAsyncWithCancellation();

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
}