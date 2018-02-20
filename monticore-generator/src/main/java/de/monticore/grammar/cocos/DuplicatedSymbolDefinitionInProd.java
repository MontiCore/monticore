/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar._ast.ASTProd;
import de.monticore.grammar.grammar._ast.ASTSymbolDefinition;
import de.monticore.grammar.grammar._cocos.GrammarASTProdCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Checks that Prods have one symbol and one scope keyword at most
 *
 * @author MB
 */
public class DuplicatedSymbolDefinitionInProd implements GrammarASTProdCoCo {
  
  public static final String ERROR_CODE = "0xA4041";
  
  public static final String ERROR_MSG_FORMAT = " Symbol or scope is mentioned more than once in the declaration '%s'.";
    
  @Override
  public void check(ASTProd a) {
    boolean isScope = false;
    boolean isSymbol = false;
    for (ASTSymbolDefinition c : a.getSymbolDefinitionList()) {
      if ((c.isGenScope() && isScope) || (c.isGenSymbol() && isSymbol)) {
        Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT, a.getName()), a.get_SourcePositionStart());
      }
      isScope |= c.isGenScope();
      isSymbol |= c.isGenSymbol();
    }
  }
  
}
