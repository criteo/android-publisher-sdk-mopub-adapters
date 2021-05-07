# Changelog

Note that all version `X.Y.Z.T` of this adapter have been tested against the matching version
`X.Y.Z` of the Publisher SDK.

## Next version

## Version 4.4.0.0
* Artifacts are now delivered through `Maven Central` repository instead of `JCenter` which is 
  deprecated.

## Version 4.3.0.0
* No changes on the adapter.

## Version 3.10.1.0
* No changes on the adapter.

## Version 3.10.0.0
* Fix visibility over the Criteo SDK at compile time for Advanced Native

## Version 3.9.0.0
* No changes on the adapter.

## Version 3.8.0.0
* Refactor non-native adapter classes to use the new consolidated API from MoPub.
* This and newer adapter versions are only compatible with 5.13.0+ MoPub SDK.
* Artifacts are now delivered through `JCenter` repository instead of a custom one: from this
version, the line `maven { url "https://pubsdk-bin.criteo.com/publishersdk/android" }` can be
removed.

## Version 3.7.0.0
* Added support for Advanced NativeAds.

## Version 3.5.0.0
* Added support for TCF2

## Version 3.4.0.1
* Changed the visibility of CriteoInitializer to package private

## Version 3.4.0.0
* Added support for MoPub's consent
