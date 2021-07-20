/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.parser.antlr;
import de.monticore.grammar.grammar._symboltable.MCGrammarSymbol;
import de.monticore.grammar.grammar._symboltable.ProdSymbol;
import de.se_rwth.commons.SourcePosition;
import de.se_rwth.commons.StringTransformations;
import de.se_rwth.commons.logging.Log;
import org.antlr.v4.Tool;
import org.antlr.v4.tool.ANTLRMessage;
import org.stringtemplate.v4.ST;

import java.util.Optional;

/**
 * ANTLR parser generator
 *
 */
public class AntlrTool extends Tool {
  
  private MCGrammarSymbol grammarSymbol;
  
  public AntlrTool(String[] args, MCGrammarSymbol grammarSymbol) {
    super(args);
    this.grammarSymbol = grammarSymbol;
  }
  
  @Override
  public void error(ANTLRMessage message) {
    createMessage(message, true);
  }
  
  @Override
  public void warning(ANTLRMessage message) {
    createMessage(message, false);
  }

  /**
   * Prints a message in MC style
   * 
   * @param message
   * @param isError
   */
  private void createMessage(ANTLRMessage message, boolean isError) {
    // Set default position
    SourcePosition position = SourcePosition.getDefaultSourcePosition();
    
    ST msgST = errMgr.getMessageTemplate(message);
    String origMessage = msgST.render();
    Log.debug(origMessage, "AnltrTool");
    
    // Change arguments corresponding to names in MC grammar
    Object[] args = message.getArgs();
    for (int i = 0; i < args.length; i++) {
      if (args[i] instanceof String) {
        String name = StringTransformations.capitalize((String) args[i]);
        Optional<ProdSymbol> rule = grammarSymbol==null?grammarSymbol.getProd(name):Optional.empty();
        if (rule.isPresent()) {
          args[i] = name;
          if (i == 0) {
            position = rule.get().getSourcePosition();
          }
        }
      }
    }
    
    // Create message
    ST messageST = message.getMessageTemplate(false);

    // Print message
    if (isError) {
      String output = "0xA0129 " + "Error from Antlr subsystem: "
              + messageST.render() + " (see e.g. www.antlr.org)";
      if (position.equals(SourcePosition.getDefaultSourcePosition())) {
        Log.error(output);
      }
      else {
        Log.error(output, position);
      }
    }
    else {
      String output = "0xA0129 " + "Warning from Antlr subsystem: "
              + messageST.render() + " (see e.g. www.antlr.org)";
      if (position.equals(SourcePosition.getDefaultSourcePosition())) {
        Log.warn(output);
      }
      else {
        Log.warn(output, position);
      }
    }
  }
  
}
