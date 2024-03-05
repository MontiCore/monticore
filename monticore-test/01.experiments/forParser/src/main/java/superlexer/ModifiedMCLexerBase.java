/* (c) https://github.com/MontiCore/monticore */
package superlexer;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Lexer;

public abstract class ModifiedMCLexerBase extends Lexer {

  public ModifiedMCLexerBase() {
  }

  public ModifiedMCLexerBase(CharStream input) {
    super(input);
  }

  public static int lexCalled = 0;

  public void lexLex() {
    lexCalled++;
  }

}
