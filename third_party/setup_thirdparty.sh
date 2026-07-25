#!/usr/bin/env bash
set -euo pipefail

THIRD_PARTY_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$THIRD_PARTY_DIR/.." && pwd)"
SKIA_DIR="$THIRD_PARTY_DIR/skia"
SKIKO_DIR="$THIRD_PARTY_DIR/skiko"
PATCHES_DIR="$THIRD_PARTY_DIR/patches"
SKIKO_VERSION="${1:-0.150.1-vulkan}"

apply_patches() {
    local target_dir="$1"
    if [[ -d "$target_dir" ]]; then
        shopt -s nullglob
        for patch in "$target_dir"/*.patch "$target_dir"/*.diff; do
            echo "Applying patch: $(basename "$patch")"
            git apply --3way "$patch"
        done
        shopt -u nullglob
    fi
}

echo "=== [1/5] Updating Git Submodules ==="
git -C "$PROJECT_ROOT" submodule update --init --recursive

echo "=== [2/5] Resetting & Patching Skia ==="
(
    cd "$SKIA_DIR"
    git reset --hard HEAD
    git clean -fd
    apply_patches "$PATCHES_DIR/skia"

    echo "Syncing Skia C++ third-party dependencies..."
    python3 tools/git-sync-deps
    python3 bin/fetch-gn
)

echo "=== [3/5] Compiling Skia C++ with Vulkan ==="
(
    cd "$SKIA_DIR"
    bin/gn gen out/Release-linux-x64 --args='
      is_official_build=true
      is_component_build=false
      skia_use_vulkan=true
      skia_enable_ganesh=true
      skia_gpu_as_extension=true
      skia_use_system_expat=false
      skia_use_system_freetype2=false
      skia_use_system_libjpeg_turbo=false
      skia_use_system_libpng=false
      skia_use_system_libwebp=false
      skia_use_system_zlib=false
      skia_use_system_harfbuzz=false
      skia_use_system_icu=false
      extra_cflags=["-DSK_USE_VULKAN"]
    '
    ninja -C out/Release-linux-x64 skia modules skia_ganesh_ext
)

echo "=== [4/5] Resetting & Patching Skiko ==="
(
    cd "$SKIKO_DIR"
    git reset --hard HEAD
    git clean -fd
    apply_patches "$PATCHES_DIR/skiko"
)

echo "=== [5/5] Compiling Skiko & Publishing to mavenLocal ==="
(
    cd "$SKIKO_DIR"
    export SKIA_DIR
    export SKIA_OUT_DIR="$SKIA_DIR/out/Release-linux-x64"

    ./gradlew :skiko:publishToMavenLocal \
        -Pversion="$SKIKO_VERSION" \
        -Pskia.dir="$SKIA_DIR"
)

echo ""
echo "================================================================="
echo "  Success! Published skiko-awt:$SKIKO_VERSION to ~/.m2/repository"
echo "================================================================="