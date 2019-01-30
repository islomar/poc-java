# Async in Java
Playground for learning about async features in Java:
    * CompletionStage (interface)
    * Future (interface)
    * CompletableFuture (class)
   
## Prerequisites
Java 11 

    
## Resources
* https://github.com/java8/Java8InAction
* https://www.baeldung.com/java-completablefuture
* https://www.callicoder.com/java-8-completablefuture-tutorial/

    
## Some notes
* The `Future` interface was added in Java 5: it provides a reference to its result that will be 
available when the computation itself is completed.
    - Future has several limitations: combining results of several async computations, waiting 
    for the completion of all of them, only waiting for the quickest one, programmatically 
    completing a Future, etc.
* In Java 8, the `CompletableFuture` class was introduced
    - It implements both Future` and `CompletableStage`
    - `supplyAsync()` get a Supplier which will be run by one of the Executors in the 
    ForkJoinPool (but you can pass a different one)
    - You can parallelize in 2 different ways:
        - Parallel streams + map: more efficient when doing compuation-heavy operations with no 
        I/O (if all threads are compute-bound, then there's no point in having more threads than 
        processor cores)
        - Streams + ComputableFuture: better when your parallel units of work involve waiting for
         I/O (including network connections). CompletableFuture gives more flexibility to match 
         the number of threads to the wait/computer. Plus, the laziness of streams can make it 
         harder to reason about when the waits actually happen.
     - `thenCompose()`: pipeline two asynchronous operations. It executes the task in the same 
     Thread than the previous task.
     - `thenCompseAsync()`: it executes the task in a different Thread than the previous task, it
      submits the task to the thread pool (each task can be handled by a different thread).
     - `join()` to wait for all the async tasks to complete.
     - `thenCombine()`: to combine two independent async calculations. It also exists a 
     `thenCombineAsync()` version.
* `CompletableStage
    - defines the contract for an asynchronous computation step that can be combined with other steps.
* Static methods `runAsync()` and `supplyAsync()` allow us to create a CompletableFuture instance
 out of Runnable and Supplier functional types correspondingly.
* The `Supplier` interface is a generic functional interface with a single method that has no arguments and returns a value of a parameterized type.
* Async API: common for I/O systems programming.


## To Do
* CompletableFuture<Void>
* CompletableFuture.supplyAsync()
* Function<Throwable, CompletableFuture<T>>
* exceptionally()

## Doubts
* Apollo, Executors and async Java...
