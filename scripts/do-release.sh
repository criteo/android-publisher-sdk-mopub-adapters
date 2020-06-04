#!/bin/bash -l

set -xEeuo pipefail

# TODO (EE-1126): Add slack integration and a job for submitting the adapters (line in Mochi)
./gradlew clean :mediation:publishReleasePublicationToAzureRepository

