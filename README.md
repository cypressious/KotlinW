# KotlinW
A small wrapper for the Kotlin compiler that can be used to execute `.kts` scripts inspired by gradlew and similar wrappers that
download the actual binaries.
More information regarding `.kts` scripts can be found [here](https://kotlinlang.org/docs/tutorials/command-line.html#using-th
e-command-line-to-run-scripts).

## Build instructions
Call `gradlew build`. This will generate a minified jar with all dependencies and a launch script for Windows. Contributions
for a Linux launch script are welcome.

## Usage
After building the minified jar, on Windows use the launch script and call `kotlinw myScript.kts`. On any other platform,
call `java -jar kotlinw-minified-1.0-SNAPSHOT.jar myScript.kts`.
This will download the latest version of the Kotlin compiler and the stdlib and will execute your script.

