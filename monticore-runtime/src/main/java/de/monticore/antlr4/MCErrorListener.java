/* (c) https://github.com/MontiCore/monticore */

package de.monticore.antlr4;

import com.google.common.collect.Lists;
import de.se_rwth.commons.SourcePosition;
import de.se_rwth.commons.StringTransformations;
import de.se_rwth.commons.logging.Log;
import org.antlr.v4.runtime.*;

import java.util.List;

public class MCErrorListener extends BaseErrorListener {
  
  protected MCParser parser = null;
  
  public MCErrorListener(MCParser parser) {
    super();
    this.parser = parser;
  }
  
  @Override
  public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
    // Improve error message
    if (recognizer instanceof Parser) {
      if ((e instanceof org.antlr.v4.runtime.InputMismatchException) && (offendingSymbol instanceof CommonToken)) {
        // add the found token type to the message
        String s = parser.getVocabulary().getSymbolicName(((CommonToken) offendingSymbol).getType());
        if (s != null && !s.isEmpty()) {
          msg += "(found: " + s + ")";
        }
      }
      // Determine rule stack without eof-rule
      List<String> stack = ((Parser) recognizer).getRuleInvocationStack();
      List<String> rules = Lists.newArrayList();
      for (int i = stack.size() - 1; i >= 0; i--) {
        if (!(i == stack.size() - 1 && stack.get(i).endsWith("_eof"))) {
          rules.add(StringTransformations.capitalize(stack.get(i)));
        }
      }
      Log.error(msg + " in rule stack: " + rules, new SourcePosition(line, charPositionInLine, parser.getFilename()));
    } else {
      Log.error(msg, new SourcePosition(line, charPositionInLine, parser.getFilename()));
    }
    parser.setErrors(true);
  }
}
