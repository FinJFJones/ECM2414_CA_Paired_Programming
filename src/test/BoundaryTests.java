package test;

import java.io.ByteArrayInputStream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import cardplayingsimulation.CardGame;

public class BoundaryTests {
    @ParameterizedTest(name="{0}")
    @MethodSource("singlePlayerTestProvider")
    void singlePlayerTests(String argument) {
        ByteArrayInputStream in = new ByteArrayInputStream(argument.getBytes());
        
        runWithInput(in);
    }

    @Test
    void instantWinTest_AllWinAtOnce_PreferredDenom() {
        ByteArrayInputStream in = new ByteArrayInputStream("4\nsrc/testPacks/boundaryTests/instantWin/instant_win_test_1.txt".getBytes());
    
        runWithInput(in);
    }

    @Test 
    void instantWinTest_Player2Wins() {
        ByteArrayInputStream in = new ByteArrayInputStream("4\nsrc/testPacks/boundaryTests/instantWin/instant_win_test_2.txt".getBytes());
    
        runWithInput(in);
    }

    @Test
    void instantWinTest_NotPreferredDenom() {
        ByteArrayInputStream in = new ByteArrayInputStream("4\nsrc/testPacks/boundaryTests/instantWin/instant_win_test_3.txt".getBytes());
    
        runWithInput(in);
    }

    @Test 
    void noPossibleWin() {
        ByteArrayInputStream in = new ByteArrayInputStream("4\nsrc/testPacks/boundaryTests/noWin/no_win_test_1.txt".getBytes());
    
        runWithInput(in);
    }

    @Test
    void noPreferredDenom_loop() {
        ByteArrayInputStream in = new ByteArrayInputStream("4\nsrc/testPacks/boundaryTests/noDenom/no_denom_test_1.txt".getBytes());
    
        runWithInput(in);
    }

    @Test
    void noPreferredDenom_random() {
        ByteArrayInputStream in = new ByteArrayInputStream("4\nsrc/testPacks/boundaryTests/noDenom/no_denom_test_2.txt".getBytes());
    
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
