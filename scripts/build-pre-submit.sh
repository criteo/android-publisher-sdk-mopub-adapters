#!/bin/bash -l

echo "Update this file to run a clean + build on android-mopub-mediation for pre-submit"
./gradlew clean build --info --stacktrace
