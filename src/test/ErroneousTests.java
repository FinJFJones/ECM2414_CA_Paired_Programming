package test;

import org.junit.jupiter.api.Test;
import java.io.ByteArrayInputStream;

import cardplayingsimulation.CardGame;

public class ErroneousTests {
    @Test
    void noPackPath() {
        ByteArrayInputStream in = new ByteArrayInputStream("4\n".getBytes());
    
        runWithInput(in);
    }

    @Test
    void noPackExists() {
        ByteArrayInputStream in = new ByteArrayInputStream("4\nsrc/testPacks/erroneousTests/erroneoustest/erroneousTest.txt".getBytes());
    
        runWithInput(in);
    }

    @Test 
    void packEmpty() {
        ByteArrayInputStream in = new ByteArrayInputStream("4\nsrc/testPacks/erroneousTests/packEmpty/pack_empty_test_1.txt".getBytes());
    
        runWithInput(in);
    }

    @Test 
    void wrongLength() {
        ByteArrayInputStream in = new ByteArrayInputStream("4\nsrc/testPacks/erroneousTests/wrongLength/wrong_length_test_1.txt".getBytes());
    
        runWithInput(in);
    }

    @Test 
    void zeroPlayers() {
        ByteArrayInputStream in = new ByteArrayInputStream("0\nsrc/testPacks/erroneousTests/playerNum/player_num_test_pack.txt".getBytes());
    
        runWithInput(in);
    }

    @Test 
    void negativePlayers() {
        ByteArrayInputStream in = new ByteArrayInputStream("-4\nsrc/testPacks/erroneousTests/playerNum/player_num_test_pack.txt".getBytes());
    
        runWithInput(in);
    }

    @Test 
    void letterPlayers() {
        ByteArrayInputStream in = new ByteArrayInputStream("error\nsrc/testPacks/erroneousTests/playerNum/player_num_test_pack.txt".getBytes());
    
        runWithInput(in);
    }

    @Test 
    void emptyPlayers() {
        ByteArrayInputStream in = new ByteArrayInputStream("\nsrc/testPacks/erroneousTests/playerNum/player_num_test_pack.txt".getBytes());
    
        runWithInput(in);
    }

    void runWithInput(ByteArrayInputStream in) {
        try {
            System.setIn(in);

            CardGame.main(new String[0]);
        } catch (InterruptedException e) {

        }
    }
}
