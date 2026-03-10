#!/bin/bash
# -----------------------------------------------
# doc-config.sh — shared config for javadoc deploy
#
# Sourced by:
#   - resolve-doc-path.sh
#   - javadoc.yml (via sanity-check job)
#
# ADD WHITELISTED BRANCHES HERE AND NOWHERE ELSE.
# main is always handled automatically.
# -----------------------------------------------

MAIN_BRANCH_NAME="main"

EXPERIMENTAL_WHITELIST=(
  "dev"
  "supporter-prerelease"
  # "new-branch"
)
