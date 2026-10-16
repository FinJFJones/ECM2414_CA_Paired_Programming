package test;

import java.io.ByteArrayInputStream;

import org.junit.jupiter.api.Test;

import CardPlayingSimulation.CardGame;

public class MainTest {
    @Test 
    void normalResult() {
        try {
            ByteArrayInputStream in = new ByteArrayInputStream("4\nsrc/testPacks/myPack.txt".getBytes());
            System.setIn(in);
            
            CardGame.main(new String[0]);
        } catch (InterruptedException e) {
            System.err.println(e);
        }
    }
}
