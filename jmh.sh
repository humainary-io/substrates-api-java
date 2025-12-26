#!/bin/sh
#
# Copyright (c) 2025 William David Louth
#
# Performance validation script:
# 1. Runs all tests
# 2. Builds JMH benchmarks
# 3. Executes benchmarks
#
# Usage:
#   ./jmh.sh                    # Run all benchmarks with default SPI
#   ./jmh.sh -l                 # List available benchmarks
#   ./jmh.sh PipeBenchmark      # Run specific benchmark class
#   ./jmh.sh -wi 5 -i 10 -f 2   # Custom JMH parameters
#
# SPI Override (via environment variables):
#   SPI_GROUP=io.example SPI_ARTIFACT=my-spi SPI_VERSION=1.0.0 ./jmh.sh
#
# The SPI provider is discovered automatically via ServiceLoader from
# META-INF/services/io.humainary.substrates.spi.CortexProvider in the SPI jar.
#

set -e

# Change to the directory containing this script
cd "$(dirname "$0")" || exit 1

# Build Maven SPI properties if provided
SPI_PROPS=""
[ -n "$SPI_GROUP" ] && SPI_PROPS="$SPI_PROPS -Dsubstrates.spi.groupId=$SPI_GROUP"
[ -n "$SPI_ARTIFACT" ] && SPI_PROPS="$SPI_PROPS -Dsubstrates.spi.artifactId=$SPI_ARTIFACT"
[ -n "$SPI_VERSION" ] && SPI_PROPS="$SPI_PROPS -Dsubstrates.spi.version=$SPI_VERSION"

echo "=== Running tests ==="
./mvnw clean install -U $SPI_PROPS

echo ""
echo "=== Building JMH benchmarks ==="
./mvnw clean package -Pjmh $SPI_PROPS

echo ""
echo "=== Running JMH benchmarks ==="
java -server -jar jmh/target/humainary-substrates-jmh-1.0.0-PREVIEW-jar-with-dependencies.jar "$@"
