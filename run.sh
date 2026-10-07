#!/bin/sh
# Compile and start the Enrollment System (needs a JDK and MySQL running)
cd "$(dirname "$0")"
CP="lib/jbcrypt-0.4.jar:lib/mysql-connector-j-9.4.0.jar"
mkdir -p out && javac -encoding UTF-8 -cp "$CP" -d out src/oopSource/*.java || exit 1
cp img/*.png out/
java -cp "out:$CP" oopSource.FinalFrameOOP
