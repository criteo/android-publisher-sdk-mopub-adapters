# Criteo Adapters for Mopub Mediation (Android)
This repository contains Criteo’s Adapter for Mopub Mediation. It must be used in conjunction with the Criteo Publisher SDK. For requirements, intructions, and other info, see [Integrating Criteo with Mopub Mediation](https://publisherdocs.criteotilt.com/sdk-android/3.1/mopub-mediation/).

# Download
Add the following maven repository into your top-level *build.gradle* file:

```kotlin
allprojects {
    repositories {
        maven { url "https://pubsdk-bin.criteo.com/publishersdk/android" }
    }
}
```

Then, in your app's module *build.gradle* file, add the following implementation configuration to the *dependencies* section:

```kotlin
    implementation 'com.criteo.mediation.mopub:criteo-adapter:3.5.0.0'
```

# License
[Apache 2.0 License](http://www.apache.org/licenses/LICENSE-2.0.html)