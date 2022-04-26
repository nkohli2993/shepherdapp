**Whats in this project**
- This project contains four screens (Splash, Login, List of Recipes, Detail of Recipe)
- I am using activities in this project only for example please use navigation graph using fragments according to your project need
- And Please follow these structure to integrate new apis do not add different code for api calls if you need any help please contact senior developers.

**More Information about MVV**
Model-View-ViewModel (ie MVVM) is a template of a client application architecture, proposed by John Gossman as an alternative to MVC and MVP patterns when using Data Binding technology. Its concept is to separate data presentation logic from business logic by moving it into particular class for a clear distinction.

**Why Promoting MVVM VS MVP:**
- ViewModel has Built in LifeCycleOwerness, on the other hand Presenter not, and you have to take this responsiblty in your side.
- ViewModel doesn't have a reference for View, on the other hand Presenter still hold a reference for view, even if you made it as weakreference.
- ViewModel survive configuration changes, while it is your own responsiblities to survive the configuration changes in case of Presenter. (Saving and restoring the UI state)

**MVVM Best Pratice:**
- Avoid references to Views in ViewModels.
- Instead of pushing data to the UI, let the UI observe changes to it.
- Distribute responsibilities, add a domain layer if needed.
- Add a data repository as the single-point entry to your data.
- Expose information about the state of your data using a wrapper or another LiveData.
- Consider edge cases, leaks and how long-running operations can affect the instances in your architecture.
- Don’t put logic in the ViewModel that is critical to saving clean state or related to data. Any call you make from a ViewModel can be the last one.


**What is Coroutines ?**
-------------------

 **Coroutines :**
Is light wight threads for asynchronous programming, Coroutines not only open the doors to
asynchronous programming, but also provide a wealth of other possibilities such as concurrency, actors, etc.

----------

**Coroutines VS RXJava**
-------------------
They're different tools with different strengths. Like a tank and a cannon, they have a lot of overlap but are more or less desirable under different circumstances.
        - Coroutines Is light wight threads for asynchronous programming.
        - RX-Kotlin/RX-Java is functional reactive programming, its core pattern relay on
        observer design pattern, so you can use it to handle user interaction with UI while you
        still using coroutines as main core for background work.

**How does Coroutines concept work ?**
------------
 - Kotlin coroutine is a way of doing things asynchronously in a sequential manner. Creating a coroutine is a lot cheaper vs creating a thread.


**When I can choose Coroutines or RX-Kotlin to do some behaviour ?**
--------------------------
 - Coroutines : *When we have concurrent tasks , like you would fetch data from Remote connections
 , database , any background processes , sure you can use RX in such cases too, but it looks like
  you use a tank to kill ant.*
 - RX-Kotlin : *When you would to handle stream of UI actions like : user scrolling , clicks ,
 update UI upon some events .....ect .*


**What is the Coroutines benefits?**
-----------------------------

 - Writing an asynchronous code is sequential manner.
 - Costing of create coroutines are much cheaper to crate threads.
 - Don't be over engineered to use observable pattern, when no need to use it.
 - parent coroutine can automatically manage the life cycle of its child coroutines for you.


**Handle Retrofit with Coroutines**
-----------------------------

 - Add Coroutines to your gradle file

>     // Add Coroutines
>     implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.2'
>     implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.2'
>     implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-core-common:1.3.2'
>     // Add Retrofit2
>     implementation 'com.squareup.retrofit2:retrofit:2.6.2'
>     implementation 'com.squareup.retrofit2:converter-gson:2.6.2'
>     implementation 'com.squareup.okhttp3:okhttp:4.2.2'


 - Make Retrofit Calls.


```kotlin
    @GET("topstories/v2/home.json")
    suspend fun fetchNews(): Response<NewsModel>
```


 - With ```async``` we create new coroutine and returns its future result as an implementation of [Deferred].
 - The coroutine builder called ```launch``` allow us to start a coroutine in background and keep working in the meantime.
 - so async will run in background then return its promised result to parent coroutine which
 created by launch.
 - when we get a result, it is up to us to do handle the result.





```kotlin
    newsMutableLiveData.postValue(Resource.Loading())
        launch {
            try {
                serviceResponse = dataRepository.requestNews()
                newsMutableLiveData.postValue(serviceResponse)
            } catch (e: Exception) {
                newsMutableLiveData.postValue(Resource.DataError(NETWORK_ERROR))
            }
        }
  ```




**Keep your code clean according to MVVM**
-----------------------------
 - Yes , liveData is easy , powerful , but you should know how to use.
 - For livedate which will emit data stream , it has to be in your
   data layer , and don't inform those observables any thing else like
   in which thread those will consume , cause it is another
 - For livedata which will emit UI binding events, it has to be in your ViewModel Layer.
 - Observers in UI Consume and react to live data values and bind it.
   responsibility , and according to `Single responsibility principle`
  in `SOLID (object-oriented design)` , so don't break this concept by
   mixing the responsibilities .
