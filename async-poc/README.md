# Async in Java
Playground for learning about async features in Java:
    * CompletionStage (interface)
    * Future (interface)
    * CompletableFuture (class)
   
## Prerequisites
Java 11 

    
## Resources
* https://www.baeldung.com/java-completablefuture

    
## Some notes
* The Future interface was added in Java 5
* In Java 8, the CompletableFuture class was introduced
    - It implements both Future and CompletableStage
* CompletableStage
    - defines the contract for an asynchronous computation step that can be combined with other steps.

    
## To Do
* CompletableFuture<Void>
* CompletableFuture.supplyAsync()
* Function<Throwable, CompletableFuture<T>>