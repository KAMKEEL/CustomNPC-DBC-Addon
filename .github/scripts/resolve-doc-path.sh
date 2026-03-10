#!/bin/bash
set -e
MAIN_BRANCH_NAME="main"
# -----------------------------------------------
# WHITELIST: Add branch names here to give them
# a page under experimental/<branch>
# `main` is handled AUTOMATICALLY
#
# Notice how they do NOT have commas.
# Gotta love bash. -Hussar
# -----------------------------------------------
EXPERIMENTAL_WHITELIST=(
  "dev"
  # "new-branch"
)
BRANCH="${GITHUB_REF#refs/heads/}"
SAFE_BRANCH="${BRANCH//\//-}"

if [ "$BRANCH" = "$MAIN_BRANCH_NAME" ]; then
  # For anyone having to deal with this bash script later on. This is the current version checking order
  # -> check if gradle.properties has a `modVersion` tag. If it does, just grab it.
  # -> check if it has the `version` tag. Grab it if it does
  # -> check build.gradle for the version tag.
  if [ -f "gradle.properties" ] && grep -q "^modVersion=" gradle.properties; then
    VERSION=$(grep "^modVersion=" gradle.properties | cut -d'=' -f2 | tr -d '[:space:]')
  elif [ -f "gradle.properties" ] && grep -q "^version=" gradle.properties; then
    VERSION=$(grep "^version=" gradle.properties | cut -d'=' -f2 | tr -d '[:space:]')
  elif [ -f "build.gradle" ]; then
    VERSION=$(grep "^version" build.gradle | head -1 | sed "s/version\s*=\s*['\"]//;s/['\"].*//;s/\s//g")
  elif [ -f "build.gradle.kts" ]; then
    VERSION=$(grep "^version" build.gradle.kts | head -1 | sed "s/version\s*=\s*\"//;s/\".*//;s/\s//g")
  else
    echo "Could not find version. Defaulting to 'unknown'."
    VERSION="unknown"
  fi

  echo "Resolved version: $VERSION"

  # Only deploy to releases/<version> — isLatest is handled by generate-index.sh
  echo "path=releases/$VERSION" >> $GITHUB_OUTPUT
  echo "extra_path=" >> $GITHUB_OUTPUT

else
  # Check if branch is in the experimental whitelist
  MATCHED=false
  for ALLOWED in "${EXPERIMENTAL_WHITELIST[@]}"; do
    if [ "$BRANCH" = "$ALLOWED" ]; then
      MATCHED=true
      break
    fi
  done

  if [ "$MATCHED" = true ]; then
    echo "path=experimental/$SAFE_BRANCH" >> $GITHUB_OUTPUT
    echo "extra_path=" >> $GITHUB_OUTPUT
    echo "Branch '$BRANCH' is whitelisted, deploying to experimental/$SAFE_BRANCH"
  else
    echo "path=" >> $GITHUB_OUTPUT
    echo "extra_path=" >> $GITHUB_OUTPUT
    echo "Branch '$BRANCH' is not whitelisted. Skipping deploy."
  fi
fi
