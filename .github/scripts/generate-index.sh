#!/bin/bash
set -e

RELEASES=()
EXPERIMENTAL=()

# Collect releases (excluding 'latest')
if [ -d "./releases" ]; then
  for dir in ./releases/*/; do
    [ -d "$dir" ] || continue
    name=$(basename "$dir")
    if [ "$name" != "latest" ]; then
      RELEASES+=("$name")
    fi
  done
fi

# Collect experimental branches
if [ -d "./experimental" ]; then
  for dir in ./experimental/*/; do
    [ -d "$dir" ] || continue
    name=$(basename "$dir")
    EXPERIMENTAL+=("$name")
  done
fi

# Sort releases in reverse semver order (newest first)
if [ ${#RELEASES[@]} -gt 0 ]; then
  IFS=$'\n' RELEASES=($(sort -rV <<< "${RELEASES[*]}")); unset IFS
fi

# ── Helper: get commit hash + date for a path ─────────────────
get_commit_info() {
  local path="$1"
  local hash date
  hash=$(git log --oneline -1 -- "$path" 2>/dev/null | awk '{print $1}')
  date=$(git log --format="%cI" -1 -- "$path" 2>/dev/null)
  echo "${hash:-} ${date:-}"
}

# ── Build releases JSON ────────────────────────────────────────
RELEASES_JSON="["

if [ -d "./releases/latest" ]; then
  read -r HASH DATE <<< "$(get_commit_info releases/latest)"
  RELEASES_JSON+="{\"id\":\"latest\",\"version\":\"latest\",\"path\":\"releases/latest\",\"hash\":\"${HASH}\",\"date\":\"${DATE}\"},"
fi

for version in "${RELEASES[@]}"; do
  read -r HASH DATE <<< "$(get_commit_info "releases/$version")"
  RELEASES_JSON+="{\"id\":\"${version}\",\"version\":\"${version}\",\"path\":\"releases/${version}\",\"hash\":\"${HASH}\",\"date\":\"${DATE}\"},"
done

RELEASES_JSON="${RELEASES_JSON%,}]"

# ── Build experimental JSON ────────────────────────────────────
EXPERIMENTAL_JSON="["

for branch in "${EXPERIMENTAL[@]}"; do
  read -r HASH DATE <<< "$(get_commit_info "experimental/$branch")"
  EXPERIMENTAL_JSON+="{\"id\":\"${branch}\",\"branch\":\"${branch}\",\"path\":\"experimental/${branch}\",\"hash\":\"${HASH}\",\"date\":\"${DATE}\"},"
done

EXPERIMENTAL_JSON="${EXPERIMENTAL_JSON%,}]"

# ── Write manifest.json ────────────────────────────────────────
cat > manifest.json << JSON
{
  "releases": ${RELEASES_JSON},
  "experimental": ${EXPERIMENTAL_JSON},
  "generated": "$(date -u '+%Y-%m-%dT%H:%M:%SZ')",
  "source": "manifest"
}
JSON

echo "manifest.json generated:"
cat manifest.json
echo ""
echo "Done."
