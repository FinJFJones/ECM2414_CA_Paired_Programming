package test.miscTests;

import org.junit.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import cardplayingsimulation.CardGame;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;




public class discardBehaviour {
    @ParameterizedTest(name = "{0}")
    @MethodSource("playerTestProvider")
    void discardBatch(String argument) throws InterruptedException, IOException {
        ByteArrayInputStream in = new ByteArrayInputStream(argument.getBytes());
        int numPlayers = Integer.parseInt(argument.split("\n")[0].trim());
        runWithInput(in,numPlayers);        
        }
    

    static Arguments[] playerTestProvider() {
        int tests = 100;
        int[] playerAmounts = {2,4,10,50};
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

    void runWithInput(ByteArrayInputStream in,int num) throws InterruptedException, IOException {
            System.setIn(in);

            CardGame.main(new String[0]);

            String cwd = System.getProperty("user.dir");
            for (int i = 1; i<=num; i++){
                String player_txt = "player"+(i)+"_output.txt";
                List<String> lines = Files.readAllLines(Paths.get(cwd+"/outputs/"+player_txt));

                for (String line : lines) {
                assert(! (line.equals("player "+(i)+" discards a "+(i)+" to deck "+((i+1)%num))));
            } 
    }
}
}