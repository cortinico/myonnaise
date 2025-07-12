# Myonnaise 🍯
[![Pre Merge Checks](https://github.com/cortinico/myonnaise/actions/workflows/pre-merge.yaml/badge.svg)](https://github.com/cortinico/myonnaise/actions/workflows/pre-merge.yaml) [![codecov](https://codecov.io/gh/cortinico/myonnaise/branch/master/graph/badge.svg)](https://codecov.io/gh/cortinico/myonnaise) [ ![Download](https://api.bintray.com/packages/cortinico/maven/myonnaise/images/download.svg) ](https://bintray.com/cortinico/maven/myonnaise/_latestVersion) ![License](https://img.shields.io/badge/license-MIT%20License-brightgreen.svg) [![Twitter](https://img.shields.io/badge/Twitter-@cortinico-blue.svg?style=flat)](http://twitter.com/cortinico)

<p align="center">
    <img width="30%" src="https://raw.githubusercontent.com/cortinico/myonnaise/master/icon_hires.png" alt="projectlogo">
</p>

An Android library to interact with your Thalmic **Myo**, written in [**Kotlin**](https://github.com/JetBrains/kotlin) and using [**RxJava2**](https://github.com/ReactiveX/RxJava). 

This repo contains also a **sample app** that showcases the usage of the library: Myo EMG Visualizer. With this app you can stream EMG Raw data from your device and **save it as a CSV**. The app is also available on the play store:

<a href='https://play.google.com/store/apps/details?id=it.ncorti.emgvisualizer&pcampaignid=MKT-Other-global-all-co-prtnr-py-PartBadge-Mar2515-1'><img alt='Get it on Google Play' width="20%" src='https://play.google.com/intl/en_gb/badges/images/generic/en_badge_web_generic.png'/></a>

**DISCLAIMER: If you don't know what a Myo is, please go here: [support.getmyo.com](https://support.getmyo.com/). Please note that you need a Myo in order to use this library/app.**

- [Myonnaise 🍯](#myonnaise-)
  - [Getting Started 👣](#getting-started-)
  - [Example 🚸](#example-)
    - [Searching for a Myo](#searching-for-a-myo)
    - [Connecting to a Myo](#connecting-to-a-myo)
    - [Sending a Command](#sending-a-command)
    - [Starting the Streaming](#starting-the-streaming)
    - [Streaming Frequency](#streaming-frequency)
    - [Keep Alive](#keep-alive)
  - [Features 🎨](#features-)
  - [Test App 📲](#test-app-)
    - [Videos](#videos)
  - [Building/Testing ⚙️](#buildingtesting-️)
    - [GitHub Actions](#github-actions)
    - [Codecov ](#codecov-)
    - [Building locally](#building-locally)
    - [Testing](#testing)
  - [Contributing 🤝](#contributing-)
  - [License 📄](#license-)

## Getting Started 👣

**Myonnaise** is distributed through [JCenter](https://bintray.com/bintray/jcenter?filterByPkgName=myonnaise). To use it you need to add the following **Gradle dependency** to your **android app gradle file** (NOT the root file).

```groovy
dependencies {
   implementation("com.ncorti:myonnaise:1.0.0")
}
```

## Example 🚸

After setting up the Gradle dependency, you will be able to access two main classes: `Myonnaise` and `Myo`. 

* [`Myonnaise`](https://github.com/cortinico/myonnaise/blob/master/myonnaise/src/main/java/com/ncorti/myonnaise/Myonnaise.kt) is the entry point where you can **trigger a bluetooth scan** to search for nearby devices. A scan will return you one or mode `BluetoothDevice` (from the [android framework](https://developer.android.com/reference/android/bluetooth/BluetoothDevice)). If you don't know your Myo's address a priori, you need to show those devices to the user and allow him to pick one.

* [`Myo`](https://github.com/cortinico/myonnaise/blob/master/myonnaise/src/main/java/com/ncorti/myonnaise/Myo.kt) is the class that will allow you to connect to your device, send commands and start the streaming. You need a `BluetoothDevice` in order to create a `Myo`.

### Searching for a Myo

First, you need to **find a Myo** with a bluetooth scan.

** ⚠️ Please note that you need to request the user the ACCESS_FINE_LOCATION permission. If not, the scan will be empty ⚠️ **

To start a bluetooth scan, you can use the `startScan()` method:

```kotlin
Myonnaise(context).startScan()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                // Do something with the found device
                println(it.address)
            })
```

This method will return a `Flowable` that will publish all the `BluetoothDevice` that are discovered nearby. Please note that the scan will stop only when you **cancel** the Flowable.

Alternatively, you can also provide a **timeout** and the scan will stop after the timeout:

```kotlin
Myonnaise(context).startScan(5, TimeUnit.MINUTES)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                // Do something with the found device
                println(it.address)
            })
```

Once you found a `BluetoothDevice` that is a Myo, you can get a `Myo` instance from it.

```kotlin
val myMyo = Myonnaise.getMyo(foundDevice)
```

### Connecting to a Myo

Connecting or disconnecting to a Myo is really easy:

```kotlin
// To Connect
myMyo.connect(getContext())

// To Disconnect
myMyo.disconnect()
```
Connecting and disconnecting are **not syncronous** operations. You have to wait that the device is successfully connected before start sending commands.
You can **get notified** of status updates using the RxJava `statusObservable`.

```kotlin
myMyo.statusObservable()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe {
            when (it) {
                MyoStatus.CONNECTED -> { /* ... */ }
                MyoStatus.CONNECTING -> { /* ... */ }
                MyoStatus.READY -> { /* ... */ }
                else -> { /* ... */ } // DISCONNECTED
            }
        }
```

In order to send command to your `Myo`, your `Myo` should be in the **READY** state. If the `Myo` is not ready, commands will be ignored.

### Sending a Command

To send a command you can use the `sendCommand()` method. For example, you can let your device vibrate with:

```kotlin
myMyo.sendCommand(CommandList.vibration1())
```

Commands will be processed by the library and sent to the device (the library has a queue to process all the commands).

List of all available commands is in the [`CommandList.kt`](https://github.com/cortinico/myonnaise/blob/master/myonnaise/src/main/java/com/ncorti/myonnaise/Myo.kt) file.

### Starting the Streaming

You can start/stop the streaming using again the `sendCommand` method:

```kotlin
// Start Streaming
myMyo.sendCommand(CommandList.emgUnfilteredOnly())

// Stop Streaming
myMyo.sendCommand(CommandList.stopStreaming())
```

You will start receiving the streaming of data as a `Flowable<FloatArray>` through the `dataFlowable()` method. To collect the data, just subscribe to the flowable:

```kotlin
myMyo.dataFlowable()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe {
            println(it) // it is an array of 8 floats.
        }
```

### Streaming Frequency

You can change the **streaming frequency** to receive less data. By default data is streamed at **200Hz** (the max supported by the device). You can subsample the data if you set the **frequency** property:

```kotlin
myMyo.frequency = 50 // Streaming at 50Hz
```

Allowed values are from 0 (reset to default) to 200.

### Keep Alive

The Myo will go to _sleep_ if he receives no intereaction within some seconds. For this reason we are sending a `CommandList.unSleep()` every 10 seconds, in order to keep the connection always on.

If you don't want this behavior, just turn off the keep alive:

```kotlin
myMyo.keepAlive = false
```

## Features 🎨

* **100% Kotlin** (but you don't need Kotlin to use it)!
* Uses **RxJava**. You don't need to poll for status update, the library will call you.
* Unleash the full Myo power, Raw Data Streaming at **200Hz**! 💪
* Small footprint: The AAR is just **36Kb**
* **API >= 21** compatible (due to BluetoothLE limitations).
* Easy to integrate (just a gradle `implementation` line).

## Test App 📲

You can find the test app (Myo Emg Visualizer) inside the `app` module. 

<p align="center">
    <img alt="test-app" src="https://i.imgur.com/lcAbyJD.png" width="30%">
</p>

This app allows you to:

* Scan for a Myo
* Connect to a Myo, control the frequency and send vibration.
* See a graph of the **EMG data**.
* Export the EMG data as a **CSV file**

Some technical features are:

* The app is 100% Kotlin.
* Architecture pattern used: MVP.
* Dagger Android to inject the views.
* Use AndroidX.
* Use the Material Design Components library.

### Videos

**Searching for a Myo**

<img alt="searching-for-a-myo" src="https://i.imgur.com/ShZP4w5.gif" width="30%"/>

**Starting the Streaming**

<img alt="start-streaming" src="https://i.imgur.com/iqvkQfr.gif" width="30%"/>

**Exporting to CSV**

<img alt="exporting-the-csv" src="https://i.imgur.com/4UXIas9.gif" width="30%"/>


## Building/Testing ⚙️

### GitHub Actions 

[![Pre Merge Checks](https://github.com/cortinico/myonnaise/actions/workflows/pre-merge.yaml/badge.svg)](https://github.com/cortinico/myonnaise/actions/workflows/pre-merge.yaml) 

This projects is built with GitHub Actions. The CI environment takes care of building the library .AAR, the example app and to run the **JUnit** tests. Test and lint reports are exposes in the **artifacts** section at the end of every build.

### Codecov 

[![codecov](https://codecov.io/gh/cortinico/myonnaise/branch/master/graph/badge.svg)](https://codecov.io/gh/cortinico/myonnaise)

Circle CI is responsible of uploading Jacoco reports to [Codecov](https://codecov.io/gh/cortinico/myonnaise). When opening a Pull Request, Codecov will post a report of the diff of the test coverage.

Please **don't ignore it**! PR with new features and **without** are likely to be discarded 😕

### Building locally

Then just clone the repo locally and build the .AAR with the following command:

```bash
git clone git@github.com:cortinico/myonnaise.git
cd myonnaise/
./gradlew build
```
The assembled .AAR (library) will be inside the **myonnaise/build/outputs/aar** folder.
The assembled .APK (application) will be inside the **app/build/outputs/apk/debug** folder.

### Testing

Once you're able to build successfully, you can run JUnit tests locally with the following command.

```bash
./gradlew test 
```
Please note that there are tests inside the `myonnaise` and the `app` module. The `app` module contains test for the presenters. The `myonnaise` module contains tests for the library.

Make sure your tests are all green ✅ locally before submitting PRs.

## Contributing 🤝

**Looking for contributors! Don't be shy.** 😁 Feel free to open issues/pull requests to help me improve this project.

* When reporting a new Issue, make sure to attach **Screenshots** of the problem you are reporting. 
* Debugging 
* When submitting a new PR, make sure tests are all green. Write new tests if necessary (would be great if the code coverage doesn't decrease).

## License 📄

This project is licensed under the MIT License - see the [License](LICENSE) file for details

