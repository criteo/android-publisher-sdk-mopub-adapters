#!/bin/bash

# This script is not meant to be run manually.
# It's triggered automatically by Gradle when publishing on Azure.

set -Eeou pipefail

function upload() {
  # TODO EE-926 Get those credentials from a vault
  export AZURE_STORAGE_ACCOUNT=pubsdkuseprod
  export AZURE_STORAGE_KEY=IBXkbamPEDzFFvLFgjL8bG5v7GOLy/2HY2xMVtgXICxSXG/AYYP57Xme9lxNgcoaznc2XGdye/zDT7fPUYrXbA==

  container_name="publishersdk"
  file_to_upload="$1"
  blob_name="android/$1"

  echo "Uploading $file_to_upload as $blob_name"
  az storage blob upload \
      --container-name "$container_name" \
      --file "$file_to_upload" \
      --name "$blob_name"
}

UPLOADED_DIR="$1"
cd "$UPLOADED_DIR"

while IFS= read -r -d $'\0' file; do
  # Remove ./ at the beginning of file: transform ./my/file into my/file
  file="$(echo "$file" | sed 's/\.\///')"

  upload "$file"
done < <(find . -type f  -print0)