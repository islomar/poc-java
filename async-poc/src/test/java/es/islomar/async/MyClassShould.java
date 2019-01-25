package es.islomar.async;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.concurrent.CancellationException;
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


public class MyClassShould {

    @Test
    public void return_string_when_complete_completablefuture_is_called() throws ExecutionException, InterruptedException {
        MyClass myClass = new MyClass();

        Future<String> stringFuture = myClass.exampleWithCompletableFuture("Hello");
        String result = stringFuture.get();

        // When calling get() we block and wait for the answer
        // get() might throw ExecutionException or InterruptedException
        assertThat(result, is("Hello"));
    }

    @Test
    public void return_string_when_complete_completablefuture_is_called_within_another_tread() throws Exception {
        MyClass myClass = new MyClass();

        Future<String> stringFuture = myClass.exampleWithCompletableFutureInAnotherThread("Hello");
        String result = stringFuture.get();

        assertThat(result, is("Hello"));
    }

    @Test
    public void throw_CancellationException_when_cancelling_a_future() {
        MyClass myClass = new MyClass();

        Future<String> stringFuture = myClass.calculateAsyncWithCancellation();

        assertThrows(CancellationException.class, () -> stringFuture.get());
    }
}