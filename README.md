# Ri News Reader

Folks at [Ridango](https://www.ridango.com/careers/) asked to write a sample Android+Kotlin app,
and here it is. Four days ago I had no idea how to code in Kotlin, and now here it is,
in its Jetpack Compose and gRPC glory.

To compile, you would need `secrets.properties` file. Copy the `.sample`,
[get your News API key](https://newsapi.org/account) and put it in there.

The app displays some news from the API and downloads more pages as you scroll down.
Tapping an article card gets you its full text, and there is also a bookmarking
button that should send a request somewhere, but the server hasn't been implemented.

Does the app work offline? Absolutely not: it needs News API to get a list of articles,
and internet access to open full article text. I could supply a local JSON file
with some old articles, or cache articles in a SQLite database, but News API feed
does not have primary keys, and juggling all the article parts becomes complicated
when the entire Android slash Compose framework is built on passing around plain strings.

I know the code is hideous. I would have written something better and pretties in Flutter
in a day, had that been a requirement. Or in Kotlin, had I couple weeks to learn
Android app architecture properly.

## Comments

* Jetpack Compose feels like a proper declarative framework, similar to Flutter.
  But unlike Flutter, here you write functions, not classes, and as a result, your
  namespace gets polluted with hundreds of weirdly named functions.
* When you run instrumented tests, you see an app in an emulator or on your phone,
  with buttons and cards tapped automatically. Ineffective (compared to Flutter),
  but very entertaining.
* Android development relies heavily on code generation. Rules for that are put
  into `app/build.gradle.kts`, along with everything else. It's a proper Kotlin
  script, which you would need to alter many times during the development, and
  every time it feels magic (in a bad sense) compared to `pubspec.yaml`.
* State model is hard to understand, and I can't say I do. In Flutter, we have
  an application context, to which everything is bound. In Android, there are
  multiple different states, some of which are reset on configuration change
  (e.g. rotating the device). People use view models, but their handling feels
  esotheric. Need to research this more.
* Tied to state, coroutines in Kotlin are way more obscure than async-await
  in Flutter. We're launching stuff in what feels like separate threads
  with `scope.launch {}`, but losing control of data with this separation.
  Like, inside a coroutine we can't be sure which calls await and which don't.

## Reference

This code was made browsing dozens of websites for every little thing.
I cannot overstate how helful [Android Courses](https://developer.android.com/courses)
and [Codelabs](https://developer.android.com/get-started/codelabs) are.
Those are a good start: both [Kotlin tours](https://kotlinlang.org/docs/kotlin-tour-welcome.html)
and the [Android Basics](https://developer.android.com/courses/android-basics-compose/course)
course. From the latter, I fast-tracked to only app building pathways.

You can peek into the code for those things:

* Storing API keys in a properties file.
* `ViewModel` state with a list downloaded from a website.
* `LazyVerticalGrid` that extends when you scroll to bottom.
* gRPC: dependencies, configuration, connections, calls.
  Funny enough, no single source on the web is comprehensive on gRPC
  dependencies.
* Navigation between pages.
* Snackbars.
* Basic WebView usage.
* Screen orientation handling.

## Author and License

Written by Ilja Zverev, published under WTFPL 2.0.
