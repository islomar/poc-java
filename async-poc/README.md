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
