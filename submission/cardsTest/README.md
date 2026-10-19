Versions used:
OpenJDK version 21.0.12.1
JUnit version 6.1.3

Build Tests:
javac -cp "junit-platform-console-standalone-6.1.3.jar:../cards.jar" -d post_submission_build src/test/*.java

Run Tests:
java -jar junit-platform-console-standalone-6.1.3.jar execute --select-package test --class-path "post_submission_build:../cards.jar" --fail-if-no-tests
