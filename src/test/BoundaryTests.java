package test;


import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;
import java.time.Duration;

import java.io.ByteArrayInputStream;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.time.Duration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.Assert;

import cardplayingsimulation.CardGame;

public class BoundaryTests {
    @ParameterizedTest(name = "{0}")
    @MethodSource("singlePlayerTestProvider")
    void singlePlayerTests(String argument) {
        ByteArrayInputStream in = new ByteArrayInputStream(argument.getBytes());

        runWithInput(in);
    }

    @Test
    void instantWinTest_AllWinAtOnce_PreferredDenom() throws FileNotFoundException {
        ByteArrayInputStream in = new ByteArrayInputStream(
                "4\nsrc/testPacks/boundaryTests/instantWin/instant_win_test_1.txt".getBytes());

        runWithInput(in);
        validateInstantWinOutput(4);
    }

    @Test
    void instantWinTest_Player2Wins() throws FileNotFoundException {
        ByteArrayInputStream in = new ByteArrayInputStream(
                "4\nsrc/testPacks/boundaryTests/instantWin/instant_win_test_2.txt".getBytes());

        runWithInput(in);
        validateInstantWinOutput(4);
    }

    @Test
    void instantWinTest_NotPreferredDenom() throws FileNotFoundException{
        ByteArrayInputStream in = new ByteArrayInputStream(
                "4\nsrc/testPacks/boundaryTests/instantWin/instant_win_test_3.txt".getBytes());

        runWithInput(in);
        validateInstantWinOutput(4);
    }

    @Test
    //@Timeout(value = 10, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    void noPossibleWin() {
        ByteArrayInputStream in = new ByteArrayInputStream(
                "4\nsrc/testPacks/boundaryTests/noWin/no_win_test_1.txt".getBytes());

        Assertions.assertThrows(AssertionFailedError.class,
            () -> {
                Assertions.assertTimeoutPreemptively(Duration.ofSeconds(10), () -> runWithInput(in));
            });
    }
    

    @Test
    void noPreferredDenom_loop() {
        ByteArrayInputStream in = new ByteArrayInputStream(
                "4\nsrc/testPacks/boundaryTests/noDenom/no_denom_test_1.txt".getBytes());

        runWithInput(in);
    }

    @Test
    void noPreferredDenom_random() {
        ByteArrayInputStream in = new ByteArrayInputStream(
                "4\nsrc/testPacks/boundaryTests/noDenom/no_denom_test_2.txt".getBytes());

        runWithInput(in);
    }

    static Arguments[] singlePlayerTestProvider() {
        int tests = 2;
        Arguments[] strings = new Arguments[tests];

        String currentString;
        for (int i = 1; i <= tests; i++) {
            currentString = "1\nsrc/testPacks/boundaryTests/1Player/1_player_test_" + i + ".txt";
            strings[i - 1] = Arguments.of(currentString);
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

    void validateInstantWinOutput(int numPlayers) throws FileNotFoundException {
        for (int i = 0; i < numPlayers; i++) {
            try (Scanner scanner = new Scanner(new File("outputs/player" + numPlayers + "_output.txt"))) {
                int lines = 0;
                while (scanner.hasNextLine()) {
                    scanner.nextLine();
                    lines++;
                }

                Assert.assertEquals(4, lines);
            }
        }
    }
}
