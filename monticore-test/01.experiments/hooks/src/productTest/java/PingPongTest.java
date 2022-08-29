/* (c) https://github.com/MontiCore/monticore */

import de.se_rwth.commons.logging.LogStub;
import org.junit.Test;

import static org.junit.Assert.*;
import de.se_rwth.commons.logging.Log;

public class PingPongTest {
  
  String message = "The automaton is not in state \"%s\"";
  String messageHandle = "\"%s\" handled wrong number of events,";
  
  @Test
  public void testPingPong(){
    PingPong pingpong = new PingPong();
    
    // assert we are in the initial state
    assertTrue(String.format(message,"NoGame"),pingpong.currentState instanceof NoGameState);
    
    // trigger startGame
    pingpong.startGame();
    
    // assert NoGame handled 1 event
    assertEquals(String.format(messageHandle,"NoGame"),1, PingPong.getNoGameState().count);
    
    // assert we are in state Ping
    assertTrue(String.format(message,"Ping"), pingpong.currentState instanceof PingState);
    
    // trigger returnBall
    pingpong.returnBall();
  
    // assert Ping handled 1 event
    assertEquals(String.format(messageHandle,"Ping"),1, PingPong.getPingState().count);
    
    // assert we are in state Pong
    assertTrue(String.format(message,"Pong"), pingpong.currentState instanceof PongState);
    
    // trigger returnBall
    pingpong.returnBall();
  
    // assert Pong handled 1 event
    assertEquals(String.format(messageHandle,"Pong"),1, PingPong.getPongState().count);
    
    // assert we are in state Ping again
    assertTrue(String.format(message,"Ping"), pingpong.currentState instanceof PingState);
    
    // trigger startGame
    pingpong.startGame();
  
    // assert stimulus was ignored
    assertEquals(String.format(messageHandle,"Ping"),1, PingPong.getPingState().count);
    assertEquals(String.format(messageHandle,"Pong"),1, PingPong.getPongState().count);
    assertEquals(String.format(messageHandle,"NoGame"),1, PingPong.getNoGameState().count);
    
    // assert we are still in state Ping (wrong input should be ignored)
    assertTrue(String.format(message,"Ping"), pingpong.currentState instanceof PingState);
    
    // trigger stopGame
    pingpong.stopGame();
  
    // assert Ping handled 2 events
    assertEquals(String.format(messageHandle,"Ping"),2, PingPong.getPingState().count);
    
    // assert we are in state NoGame
    assertTrue(String.format(message,"NoGame"), pingpong.currentState instanceof NoGameState);
  
    assertTrue(Log.getFindings().isEmpty());
  }
  
}


