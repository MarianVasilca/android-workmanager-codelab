WorkManager Codelab Solution
===================================

This repository contains the solution code for the [WorkManager Codelab](https://codelabs.developers.google.com/codelabs/android-workmanager):

Introduction
------------

At I/O 2018 Google announced [Android Jetpack](https://developer.android.com//jetpack/), a collection of libraries, tools and architectural guidance to accelerate and simplify the development of great Android apps. One of those libraries is the [WorkManager library](https://developer.android.com/topic/libraries/architecture/workmanager). The WorkManager library provides a unified API for deferrable one-off or recurring background tasks that need guaranteed execution. You can learn more by reading the [WorkManager documentation](https://developer.android.com/topic/libraries/architecture/workmanager).


Pre-requisites
--------------

* Android Studio 3.0 or later and you know how to use it.

* Make sure Android Studio is updated, as well as your SDK and Gradle.
Otherwise, you may have to wait for a while until all the updates are done.

* A device or emulator that runs API level 26

You need to be solidly familiar with the Kotlin programming language,
object-oriented design concepts, and Android Development Fundamentals.
In particular:

* Basic layouts and widgets
* Some familiarity with Uris and File I/O
* Familiarity with [LiveData](https://developer.android.com/topic/libraries/architecture/livedata) and [ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel)

Getting Started
---------------

1. [Install Android Studio](https://developer.android.com/studio/install.html),
if you don't already have it.
2. Download the example.
2. Import the example into Android Studio.
3. Build and run the example.

Work Manager
------------

WorkManager is a simple, but incredibly flexible library that has many additional benefits. These include:

1. Support for both asynchronous one-off and periodic tasks
2. Support for constraints such as network conditions, storage space, and charging status
3. Chaining of complex work requests, including running work in parallel
4. Output from one work request used as input for the next
5. Handles API level compatibility back to API level 14
6. Works with or without Google Play services
7. Follows system health best practices
8. LiveData support to easily display work request state in UI

What I learned?
---------------

I learned about:

1. Adding WorkManager to your Project
2. Scheduling a OneOffWorkRequest
3. Input and Output parameters
4. Chaining work together WorkRequests
5. Naming Unique WorkRequest chains
6. Tagging WorkRequests
7. Displaying WorkStatus in the UI
8. Cancelling WorkRequests
9. Adding constraints to a WorkRequest


Important
---------

Version '1.0.0-alpha01' of Work Manager library has a [bug](https://github.com/googlesamples/android-architecture-components/issues/356) that makes a NullPointerException.

License
-------

Copyright 2018 Google, Inc and Marian Vasilca.

Licensed to the Apache Software Foundation (ASF) under one or more contributor
license agreements.  See the LICENSE file distributed with this work for
additional information regarding copyright ownership.  The ASF licenses this
file to you under the Apache License, Version 2.0 (the "License"); you may not
use this file except in compliance with the License.  You may obtain a copy of
the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
License for the specific language governing permissions and limitations under
the License.