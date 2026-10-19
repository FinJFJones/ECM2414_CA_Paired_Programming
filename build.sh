javac -d build src/cardplayingsimulation/*.java

jar --create --file dist/cards.jar --main-class cardplayingsimulation.CardGame -C build .
