/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.transformation;

import de.monticore.grammar.grammar._ast.*;
import de.monticore.grammar.grammar_withconcepts._parser.Grammar_WithConceptsParser;
import de.monticore.utils.ASTTraverser;
import de.se_rwth.commons.logging.Log;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

/**
 * Static facade for the transformation of MC AST.
 */
public class GrammarTransformer {
  
  private GrammarTransformer() {
    // noninstantiable
  }
  
  public static void transform(ASTMCGrammar grammar) {
    removeNonTerminalSeparators(grammar);
  }
  
  /**
   * The shortcut NonTerminalSeparator is replaced by the detailed description.
   * Example: List(Element || ',')* ==> (List:Element (',' List:Element)+)
   */
  public static void removeNonTerminalSeparators(ASTMCGrammar grammar) {
    Map<ASTNonTerminalSeparator, ASTAlt> map = new HashMap<ASTNonTerminalSeparator, ASTAlt>();
    RuleComponentListFinder componentListTransformer = new RuleComponentListFinder(map);
    ASTTraverser traverser = new ASTTraverser(componentListTransformer);
    
    // execute the search
    traverser.traverse(grammar);
    
    // execute the transformation
    for (Entry<ASTNonTerminalSeparator, ASTAlt> entry : map.entrySet()) {
      Log.debug("Find NonTerminalSeparator", GrammarTransformer.class.getName());
      Optional<ASTBlock> block = transform(entry.getKey());
      if (block.isPresent()) {
        ASTAlt parent = entry.getValue();
        int ind = parent.getComponentList().indexOf(entry.getKey());
        if (ind >= 0) {
          Log.debug("Remove NonTerminalSeparator", GrammarTransformer.class.getName());
          parent.getComponentList().remove(ind);
          Log.debug("Added new generated block", GrammarTransformer.class.getName());
          parent.getComponentList().add(ind, block.get());
        }
        else {
          Log.error("0xA1009 Can't transform grammar");
        }
      }
    }
  }

  /**
   * @param nonTerminalSep
   * @return
   */
  private static Optional<ASTBlock> transform(ASTNonTerminalSeparator nonTerminalSep) {
    String name = "";
    if (nonTerminalSep.isPresentUsageName()) {
      name = nonTerminalSep.getUsageName() + ":";
    }
    String plusKeywords = (nonTerminalSep.isPlusKeywords()) ? "&" : "";
    String iteration = (nonTerminalSep.getIteration() == ASTConstantsGrammar.STAR) ? "?" : "";
    
    String extendedList = "(%usageName% %nonTerminal% %plusKeywords% (\"%terminal%\" %usageName% %nonTerminal% %plusKeywords%)*)%iterator%";
    extendedList = extendedList.replaceAll("%usageName%", name);
    extendedList = extendedList.replaceAll("%nonTerminal%", nonTerminalSep.getName());
    extendedList = extendedList.replaceAll("%plusKeywords%", plusKeywords);
    extendedList = extendedList.replaceAll("%terminal%", nonTerminalSep.getSeparator());
    extendedList = extendedList.replaceAll("%iterator%", iteration);
    
    Grammar_WithConceptsParser parser = new Grammar_WithConceptsParser();
    Optional<ASTBlock> block = null;
    try {
      Log.debug("Create ast for " + extendedList, GrammarTransformer.class.getName());
      block = parser.parseBlock(new StringReader(extendedList));
      if (parser.hasErrors()) {
        Log.error("0xA0362 RecognitionException during parsing " + extendedList);
      }
    }
    catch (IOException e) {
      Log.error("0xA0361 IOException during parsing " + extendedList);
    }
    return block;
  }
  
}
