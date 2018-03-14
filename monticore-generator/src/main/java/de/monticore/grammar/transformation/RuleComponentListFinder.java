/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.transformation;

import java.util.Map;

import de.monticore.annotations.Visit;
import de.monticore.grammar.grammar._ast.ASTAlt;
import de.monticore.grammar.grammar._ast.ASTNonTerminalSeparator;
import de.monticore.grammar.grammar._ast.ASTRuleComponent;
import de.monticore.utils.ASTTraverser;

public class RuleComponentListFinder implements ASTTraverser.Visitor{
  
  private Map<ASTNonTerminalSeparator, ASTAlt> map;
  
  /**
   * Constructor for de.monticore.grammar.transformation.RuleComponentListFinder.
   * @param grammar
   * @param map
   */
  public RuleComponentListFinder(Map<ASTNonTerminalSeparator, ASTAlt> map) {
    super();
    this.map = map;
  }
  
  @Visit
  private void find(ASTAlt alt) {
    for (ASTRuleComponent component: alt.getComponentList()) {
      if (component instanceof ASTNonTerminalSeparator) {
        map.put((ASTNonTerminalSeparator) component, alt);
      }
    }
  }

  
}
