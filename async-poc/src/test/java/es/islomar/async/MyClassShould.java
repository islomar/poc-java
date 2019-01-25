package es.islomar.async;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import java.util.concurrent.Future;
import org.testng.annotations.Test;

/**
 * Things to understand:
 *      * CompletionStage
 *      * thenApply()
 *      * thenCompose()
 *      * exceptionally()
 *      * supplyAsync()
 *      * Best practices for exception handling
 */

@Test
public class MyClassShould {

    public void try_complete_completablefuture() throws Exception {
        final MyClass myClass = new MyClass();

        final Future<String> stringFuture = myClass.exampleWithCompletableFuture("Hello");

        assertThat(stringFuture.get(), is("Hello"));
    }

    public void try_complete_completablefuture_within_another_tread() throws Exception {
        final MyClass myClass = new MyClass();

        final Future<String> stringFuture = myClass.exampleWithCompletableFutureInAnotherThread("Hello");

        assertThat(stringFuture.get(), is("Hello"));
    }
}