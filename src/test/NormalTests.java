package test;

import java.io.ByteArrayInputStream;

import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import cardplayingsimulation.CardGame;

public class NormalTests {
    @ParameterizedTest(name = "{0}")
    @MethodSource("playerTestProvider")
    void normalResultBatch(String argument) {
        ByteArrayInputStream in = new ByteArrayInputStream(argument.getBytes());
        
        runWithInput(in);
    }

    static Arguments[] playerTestProvider() {
        int tests = 100;
        int[] playerAmounts = {2, 4, 10, 50, 100};
        Arguments[] strings = new Arguments[tests*playerAmounts.length];

        String currentString;
        for (int i = 0; i < playerAmounts.length; i++) {
            for (int j = 1; j <= tests; j++) {
                currentString = playerAmounts[i] + "\nsrc/testPacks/normalTests/" + playerAmounts[i] + "Player/" + playerAmounts[i] + "_player_test_" + j + ".txt";
                strings[(i*tests)+j-1] = Arguments.of(currentString);
            }
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
