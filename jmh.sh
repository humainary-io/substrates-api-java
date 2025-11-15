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
#   ./jmh.sh                    # Run all benchmarks with default settings
#   ./jmh.sh -l                 # List available benchmarks
#   ./jmh.sh PipeBenchmark      # Run specific benchmark class
#   ./jmh.sh -wi 5 -i 10 -f 2   # Custom JMH parameters
#

# Change to the directory containing this script
cd "$(dirname "$0")" || exit 1

echo "=== Running tests ==="
./mvnw clean install -U

echo ""
echo "=== Building JMH benchmarks ==="
./mvnw clean package -Pjmh

echo ""
echo "=== Running JMH benchmarks ==="
java -jar -server jmh/target/humainary-substrates-jmh-1.0.0-PREVIEW-jar-with-dependencies.jar "$@"
