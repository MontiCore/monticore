/* (c) https://github.com/MontiCore/monticore */

package de.monticore.antlr4;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

import de.se_rwth.commons.SourcePosition;
import de.se_rwth.commons.logging.Log;

public class MCErrorListener extends BaseErrorListener {
  
  protected MCParser parser = null;
  
  public MCErrorListener(MCParser parser) {
    super();
    this.parser = parser;
  }
  
  @Override
  public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {

    Log.error(msg, new SourcePosition(line, charPositionInLine, parser.getFilename()));

    parser.setErrors(true);
  }
}
