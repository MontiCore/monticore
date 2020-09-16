/* (c) https://github.com/MontiCore/monticore */

import org.junit.Test;

import static org.junit.Assert.*;

public class PingPongTest {
  
  String message = "The automaton is not in state \"%s\"";
  
  @Test
  public void testPingPong(){
    PingPong pingpong = new PingPong();
    
    // assert we are in the initial state
    assertTrue(String.format(message,"NoGame"),pingpong.currentState instanceof NoGameState);
    
    // trigger startGame
    pingpong.startGame();
    
    // assert we are in state Ping
    assertTrue(String.format(message,"Ping"), pingpong.currentState instanceof PingState);
    
    // trigger returnBall
    pingpong.returnBall();
    
    // assert we are in state Pong
    assertTrue(String.format(message,"Pong"), pingpong.currentState instanceof PongState);
    
    // trigger returnBall
    pingpong.returnBall();
    
    // assert we are in state Ping again
    assertTrue(String.format(message,"Ping"), pingpong.currentState instanceof PingState);
    
    // trigger startGame
    pingpong.startGame();
    
    // assert we are still in state Ping (wrong input should be ignored)
    assertTrue(String.format(message,"Ping"), pingpong.currentState instanceof PingState);
    
    // trigger stopGame
    pingpong.stopGame();
    
    // assert we are in state NoGame
    assertTrue(String.format(message,"NoGame"), pingpong.currentState instanceof NoGameState);
    
  }
  
}


