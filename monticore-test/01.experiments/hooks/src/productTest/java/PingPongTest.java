/* (c) https://github.com/MontiCore/monticore */


import de.monticore.runtime.junit.MCAssertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PingPongTest {
  
  String message = "The automaton is not in state \"%s\"";
  String messageHandle = "\"%s\" handled wrong number of events,";
  
  @Test
  public void testPingPong(){
    PingPong pingpong = new PingPong();
    
    // assert we are in the initial state
    assertInstanceOf(NoGameState.class, pingpong.currentState, String.format(message, "NoGame"));
    
    // trigger startGame
    pingpong.startGame();
    
    // assert NoGame handled 1 event
    assertEquals(1, PingPong.getNoGameState().count, String.format(messageHandle,"NoGame"));
    
    // assert we are in state Ping
    assertInstanceOf(PingState.class, pingpong.currentState, String.format(message, "Ping"));
    
    // trigger returnBall
    pingpong.returnBall();
  
    // assert Ping handled 1 event
    assertEquals(1, PingPong.getPingState().count, String.format(messageHandle,"Ping"));
    
    // assert we are in state Pong
    assertInstanceOf(PongState.class, pingpong.currentState, String.format(message, "Pong"));
    
    // trigger returnBall
    pingpong.returnBall();
  
    // assert Pong handled 1 event
    assertEquals(1, PingPong.getPongState().count, String.format(messageHandle,"Pong"));
    
    // assert we are in state Ping again
    assertInstanceOf(PingState.class, pingpong.currentState, String.format(message, "Ping"));
    
    // trigger startGame
    pingpong.startGame();
  
    // assert stimulus was ignored
    assertEquals(1, PingPong.getPingState().count, String.format(messageHandle,"Ping"));
    assertEquals(1, PingPong.getPongState().count, String.format(messageHandle,"Pong"));
    assertEquals(1, PingPong.getNoGameState().count, String.format(messageHandle,"NoGame"));
    
    // assert we are still in state Ping (wrong input should be ignored)
    assertInstanceOf(PingState.class, pingpong.currentState, String.format(message, "Ping"));
    
    // trigger stopGame
    pingpong.stopGame();
  
    // assert Ping handled 2 events
    assertEquals(2, PingPong.getPingState().count, String.format(messageHandle,"Ping"));
    
    // assert we are in state NoGame
    assertInstanceOf(NoGameState.class, pingpong.currentState, String.format(message, "NoGame"));
  
    MCAssertions.assertNoFindings();
  }
  
}


