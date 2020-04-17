# Publishing the adapter

## Bumping the version number

The `version` should be bumped in the `mediation/build.gradle` file.

You can also add some words in the `CHANGELOG.md` file.

## Building and publishing the adapter to the production repository

```shell script
./gradlew clean :mediation:publishReleasePublicationToAzureRepository
```

## Publishing the adapter on GitHub

EE-924 TODO