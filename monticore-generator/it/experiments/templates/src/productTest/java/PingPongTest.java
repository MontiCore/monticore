/* (c) https://github.com/MontiCore/monticore */

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PingPongTest {
  
  @Test
  public void testPingPong(){
    PingPong pingpong = new PingPong();
    
    // assert we are in the initial state
    assertTrue(pingpong.currentState instanceof NoGameState);
  
    // trigger startGame
    pingpong.startGame();
  
    // assert we are in state Ping
    assertTrue(pingpong.currentState instanceof PingState);
  
    // trigger returnBall
    pingpong.returnBall();
  
    // assert we are in state Pong
    assertTrue(pingpong.currentState instanceof PongState);
    
    // trigger returnBall
    pingpong.returnBall();
  
    // assert we are in state Ping again
    assertTrue(pingpong.currentState instanceof PingState);
  
    // trigger startGame
    pingpong.startGame();
  
    // assert we are still in state Ping (wrong input should be ignored)
    assertTrue(pingpong.currentState instanceof PingState);
  
    // trigger stopGame
    pingpong.stopGame();
  
    // assert we are in state NoGame
    assertTrue(pingpong.currentState instanceof NoGameState);
  
  }

}


