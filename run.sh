#!/bin/bash
# Launch script for Cricket Match Scorer & Stats Tracker
# Requires: Java 11+ and openjfx installed (apt install openjfx)

JFX=/usr/share/openjfx/lib

java -cp "$JFX/javafx.controls.jar:$JFX/javafx.base.jar:$JFX/javafx.graphics.jar:$JFX/javafx.fxml.jar:cricket-scorer.jar" \
     com.cricket.ui.CricketApp
