# ProGuard/R8 rules for the Fetch Exercise app
#
# This file contains project-specific rules for code shrinking, obfuscation, and optimization.
# You can control the set of applied configuration files using the proguardFiles setting in build.gradle.
#
# For more details, see:
#   https://developer.android.com/studio/build/shrink-code
#   https://developer.android.com/guide/developing/tools/proguard.html

# ------------------------------------------------------------------------------
# WebView JavaScript Interface
# ------------------------------------------------------------------------------
# If your project uses WebView with JavaScript interfaces, uncomment and specify
# the fully qualified class name to the JavaScript interface class to prevent it
# from being removed or obfuscated.
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# ------------------------------------------------------------------------------
# Debugging: Preserve Line Number Information
# ------------------------------------------------------------------------------
# Uncomment this to preserve the line number information for debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to hide the original source file name.
#-renamesourcefileattribute SourceFile

# ------------------------------------------------------------------------------
# Add additional rules below as needed for libraries, reflection, or custom classes
# ------------------------------------------------------------------------------