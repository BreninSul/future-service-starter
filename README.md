The service is designed to make it easier to work with Future. It helps to register and terminate tasks by id with setting a timeout for a specific task or depending on the class (otherwise a standard value will be used)

To use this lib add repository

````kotlin

repositories {
...
    maven {
        name = "GitHub"
        url = uri("https://maven.pkg.github.com/BreninSul/future-service-starter")
    }
...
}
````

And dependency


````kotlin
dependencies {
    ...
    implementation("com.github.breninsul:future-starter:1.0.0")
    ...
}

````