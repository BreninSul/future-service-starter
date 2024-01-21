The service is designed to make it easier to work with Future. It helps to register and terminate tasks by id with setting a timeout for a specific task or depending on the class (otherwise a standard value will be used)
Contains Spring Boot Autoconfiguration, but can be used out-of Spring context

To use this library, add the following repository:

````kotlin

repositories {
//Other repositories
    maven {
        name = "GitHub"
        url = uri("https://maven.pkg.github.com/BreninSul/future-service-starter")
    }
//Other repositories
}
````

 Next, add the following dependency:

````kotlin
dependencies {
//Other dependencies
    implementation("com.github.breninsul:future-starter:1.0.0")
//Other dependencies
}

````
# Example of usage

````kotlin
//1. Crete service instance or autowire it with Spring DI mechanism
val futureService = DefaultFutureService(mapOf(CustomDTO::class to Duration.ofMinutes(10)), Duration.ofMinutes(1))
//2. Register task
val future = futureService.registerTask<String>(taskId,Duration.ofMillis(100))
//2.1 Get result where it's needed
val result=future.get()
//2.2 Or wait till the result at once 
val result = futureService.waitResult<String>(taskId)
//3 In any place of application (Callback endpoint as example) complete the task 
futureService.complete<String>(taskId,okResult)
//3.1 Or complete with error if it's needed
futureService.completeExceptionally<String>(taskId,IllegalStateException("Oh!"))
````