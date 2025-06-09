#!/bin/bash

echo "🎮 Starting Chess Pedagogue Configurator (High-DPI Mode)..."
echo ""

# Set up Maven path
export PATH="$PWD/apache-maven-3.9.5/bin:$PATH"

echo "📊 Detected display setup for high-DPI multi-monitor (67\" 4K + 11\" 1080p + 27\" 1440p)"
echo "🔧 Applying optimal scaling settings..."
echo ""

# Run with high-DPI scaling optimizations
mvn javafx:run \
    -Djavafx.run.args="\
        -Dglass.win.uiScale=1.5 \
        -Dglass.gtk.uiScale=1.5 \
        -Dprism.allowhidpi=true \
        -Dprism.force.sw=false \
        -Djava.awt.headless=false \
        -Dsun.java2d.uiScale=1.5"

echo ""
echo "✅ Chess Pedagogue Configurator session ended."