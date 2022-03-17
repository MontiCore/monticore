/* (c) https://github.com/MontiCore/monticore */

import java.io.IOException;
import java.util.*;

public class SimpleAutomata {
  
  /**
   *
   */
  public static void main(String[] args) {
     List<Character> e = new ArrayList<>();
     System.out.println("  Result: " +simple(e) +" on: "+e);
     e.add('a');
     System.out.println("  Result: " +simple(e) +" on: "+e);
     e.add('b');
     e.add('a');
     System.out.println("  Result: " +simple(e) +" on: "+e);
     e.add('b');
     System.out.println("  Result: " +simple(e) +" on: "+e);
  }
  // Implements the simple Automaton in Java:
  static
  boolean simple(List<Character> input) {
    int state = 1;
    for (char item : input) {
       System.out.println(item);
       switch(state) {
         case 1: switch(item) {
                   case 'a': state = 2; break;
                 }; break;
         case 2: switch(item) {
                   case 'b': state = 1; break;
                 }; break;
       }
    }
    return state == 2;
  }

}

