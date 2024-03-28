/* (c) https://github.com/MontiCore/monticore */

package de.monticore.antlr4;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Lexer;


public abstract class MCLexer extends Lexer {
  
  public MCLexer(CharStream input) {
    super(input);
  }

  public void newline() {}
  
}
