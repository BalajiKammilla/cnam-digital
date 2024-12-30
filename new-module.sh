#!/usr/bin/env bash

export MODULE_NAME=$1

mkdir -p "$MODULE_NAME"/src/main/kotlin
mkdir -p "$MODULE_NAME"/src/main/resources
mkdir -p "$MODULE_NAME"/src/test/kotlin
mkdir -p "$MODULE_NAME"/src/test/resources
touch "$MODULE_NAME"/build.gradle.kts

{
  echo "plugins {"
  echo "  id(\"cnam.kotlin\")"
  echo "}"
  echo ""
  echo "dependencies {"
  echo ""
  echo "}"
} >> "$MODULE_NAME"/build.gradle.kts

echo "include(\"$MODULE_NAME\")" >> settings.gradle.kts
