/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.grammar_withconcepts._parser;

import de.monticore.grammar.grammar._ast.ASTAlt;
import de.monticore.grammar.grammar._ast.ASTNonTerminalSeparator;
import de.monticore.grammar.grammar._ast.ASTRuleComponent;
import de.monticore.grammar.grammar._visitor.GrammarVisitor2;

import java.util.Map;

public class RuleComponentListFinder implements GrammarVisitor2 {
  
  private Map<ASTNonTerminalSeparator, ASTAlt> map;
  
  /**
   * Constructor for de.monticore.grammar.grammar_withconcepts._parser.RuleComponentListFinder.
   * @param map
   */
  public RuleComponentListFinder(Map<ASTNonTerminalSeparator, ASTAlt> map) {
    super();
    this.map = map;
  }
  
  public void visit(ASTAlt alt) {
    for (ASTRuleComponent component: alt.getComponentList()) {
      if (component instanceof ASTNonTerminalSeparator) {
        map.put((ASTNonTerminalSeparator) component, alt);
      }
    }
  }

  
}
