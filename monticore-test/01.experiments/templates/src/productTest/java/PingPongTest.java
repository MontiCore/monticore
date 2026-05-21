/* (c) https://github.com/MontiCore/monticore */

import de.monticore.runtime.junit.MCAssertions;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class PingPongTest {
  
  String message = "The automaton is not in state \"%s\"";
  
  @Test
  public void testPingPong(){
    PingPong pingpong = new PingPong();
    
    // assert we are in the initial state
    assertInstanceOf(NoGameState.class, pingpong.currentState, String.format(message, "NoGame"));
  
    // trigger startGame
    pingpong.startGame();
  
    // assert we are in state Ping
    assertInstanceOf(PingState.class, pingpong.currentState, String.format(message, "Ping"));
  
    // trigger returnBall
    pingpong.returnBall();
  
    // assert we are in state Pong
    assertInstanceOf(PongState.class, pingpong.currentState, String.format(message, "Pong"));
    
    // trigger returnBall
    pingpong.returnBall();
  
    // assert we are in state Ping again
    assertInstanceOf(PingState.class, pingpong.currentState, String.format(message, "Ping"));
  
    // trigger startGame
    pingpong.startGame();
  
    // assert we are still in state Ping (wrong input should be ignored)
    assertInstanceOf(PingState.class, pingpong.currentState, String.format(message, "Ping"));
  
    // trigger stopGame
    pingpong.stopGame();
  
    // assert we are in state NoGame
    assertInstanceOf(NoGameState.class, pingpong.currentState, String.format(message, "NoGame"));
  
    MCAssertions.assertNoFindings();
  }

}


