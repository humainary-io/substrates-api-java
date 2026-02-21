#!/bin/sh
#
# Copyright (c) 2025 William David Louth
#
# TCK validation script:
# Runs the Technology Compatibility Kit tests against the configured SPI.
#
# Usage:
#   ./tck.sh                    # Run with default (alpha) SPI
#
# SPI Override (via environment variables):
#   SPI_GROUP=io.example SPI_ARTIFACT=my-spi SPI_VERSION=1.0.0 ./tck.sh
#
# The SPI provider is discovered automatically via ServiceLoader from
# META-INF/services/io.humainary.substrates.spi.CortexProvider in the SPI jar.
#

# Change to the directory containing this script
cd "$(dirname "$0")" || exit 1

# Build Maven SPI properties if provided
SPI_PROPS=""
[ -n "$SPI_GROUP" ] && SPI_PROPS="$SPI_PROPS -Dsubstrates.spi.groupId=$SPI_GROUP"
[ -n "$SPI_ARTIFACT" ] && SPI_PROPS="$SPI_PROPS -Dsubstrates.spi.artifactId=$SPI_ARTIFACT"
[ -n "$SPI_VERSION" ] && SPI_PROPS="$SPI_PROPS -Dsubstrates.spi.version=$SPI_VERSION"

./mvnw clean install -U -Dguice_custom_class_loading=CHILD -Dtck $SPI_PROPS
