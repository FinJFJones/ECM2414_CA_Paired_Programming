package test;

import java.io.ByteArrayInputStream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import game.CardGame;

public class BoundaryTests {
    @ParameterizedTest(name="{0}")
    @MethodSource("singlePlayerTestProvider")
    void boundaryResultBatch(String argument) {
        ByteArrayInputStream in = new ByteArrayInputStream(argument.getBytes());
        
        runWithInput(in);
    }

    static Arguments[] singlePlayerTestProvider() {
        int tests = 2;
        Arguments[] strings = new Arguments[tests];

        String currentString;
            for (int i = 1; i <= tests; i++) {
                currentString = "1\nsrc/testPacks/boundaryTests/1Player/1_player_test_" + i + ".txt";
                strings[i-1] = Arguments.of(currentString);
            }

        return strings;
    }

    void runWithInput(ByteArrayInputStream in) {
        try {
            System.setIn(in);

            CardGame.main(new String[0]);
        } catch (InterruptedException e) {

        }
    }
}
