/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.parser.antlr;

import de.monticore.ast.ASTNode;
import de.monticore.grammar.grammar._ast.ASTNonTerminal;
import de.monticore.grammar.grammar._ast.ASTProd;
import de.monticore.grammar.grammar._symboltable.MCGrammarSymbol;
import de.monticore.grammar.grammar._symboltable.ProdSymbol;
import de.se_rwth.commons.SourcePosition;
import de.se_rwth.commons.StringTransformations;
import de.se_rwth.commons.logging.Log;
import org.antlr.v4.Tool;
import org.antlr.v4.tool.*;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.misc.MultiMap;

import java.util.*;

/**
 * ANTLR parser generator
 *
 */
public class AntlrTool extends Tool {
  
  protected MCGrammarSymbol grammarSymbol;
  protected Map<ASTProd, Map<ASTNode, String>> tmpNameDict;
  protected Map<ASTNonTerminal, Set<Integer>> nonTerminalToParserStates = new LinkedHashMap<>();

  public AntlrTool(String[] args, MCGrammarSymbol grammarSymbol, Map<ASTProd, Map<ASTNode, String>> tmpNameDict) {
    super(args);
    this.grammarSymbol = grammarSymbol;
    this.tmpNameDict = tmpNameDict;
  }

  public Map<ASTNonTerminal, Set<Integer>> getNonTerminalToParserStates() {
    return nonTerminalToParserStates;
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
  protected void createMessage(ANTLRMessage message, boolean isError) {
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
        Optional<ProdSymbol> rule = grammarSymbol!=null?grammarSymbol.getProd(name):Optional.empty();
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
      String output = "0xA1129 " + "Error from Antlr subsystem: "
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

  @Override
  public void processNonCombinedGrammar(Grammar g, boolean gencode) {
    super.processNonCombinedGrammar(g, gencode);
    calculateStatesForNonTerminals(g);
  }

  /**
   * Calculates all Antlr parser states for each NonTerminal of the passed grammar.
   * @param g the current Antlr Grammar
   */
  private void calculateStatesForNonTerminals(Grammar g) {
    if(g.isParser() || g.isCombined()){
      for (Map.Entry<ASTProd, Map<ASTNode, String>> outer : tmpNameDict.entrySet()) {
        if(!outer.getValue().isEmpty()) {
          for (Map.Entry<ASTNode, String> inner : outer.getValue().entrySet()) {
            ASTNode key = inner.getKey();
            if(key instanceof ASTNonTerminal){
              ASTNonTerminal nonTerminal = (ASTNonTerminal) key;
              String ruleName = outer.getKey().getName();
              ruleName = ruleName.substring(0, 1).toLowerCase() + ruleName.substring(1);
              nonTerminalToParserStates.put(
                      nonTerminal,
                      calculateStateForTmpName(g, ruleName, inner.getValue())
              );
            }
          }
        }
      }
    }
  }

  /**
   * Calculates all Antlr parser states for the NonTerminal with the passed temporary name, within the current rule and grammar
   * @param g the current Grammar
   * @param ruleName the name of the current Rule
   * @param tmpName the temporary name of the NonTerminal
   * @return all Antlr parser states that correspond to the NonTerminal
   */
  private Set<Integer> calculateStateForTmpName(Grammar g, String ruleName, String tmpName){
    Rule r = g.getRule(ruleName);
    if(r == null){ return Collections.emptySet(); }

    MultiMap<String, LabelElementPair> elementLabelDefs = r.getElementLabelDefs();
    if(!elementLabelDefs.containsKey(tmpName)) { return Collections.emptySet(); }

    Set<Integer> res = new LinkedHashSet<>();

    for (LabelElementPair pair : elementLabelDefs.get(tmpName)) {
      if(pair.type == LabelType.TOKEN_LABEL || pair.type == LabelType.TOKEN_LIST_LABEL){
        res.add(pair.element.atnState.stateNumber);
      }
    }

    return res;
  }
}
