#!/bin/sh
#
# Copyright (c) 2025 William David Louth
#

# Change to the directory containing this script
cd "$(dirname "$0")" || exit 1

./mvnw clean install -U -Dguice_custom_class_loading=CHILD -Dtck
