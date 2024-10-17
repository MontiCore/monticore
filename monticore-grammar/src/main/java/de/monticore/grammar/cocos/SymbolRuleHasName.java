/* (c) https://github.com/MontiCore/monticore */
package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar._ast.ASTSymbolRule;
import de.monticore.grammar.grammar._cocos.GrammarASTSymbolRuleCoCo;
import de.se_rwth.commons.logging.Log;

public class SymbolRuleHasName implements GrammarASTSymbolRuleCoCo {

  public static final String ERROR_CODE = "0xA0118";

  public static final String ERROR_MSG = " SymbolRule Attribute at %s does not have a name.";

  @Override
  public void check(ASTSymbolRule node) {
    node.getAdditionalAttributeList().forEach(a -> {
      if (!a.isPresentName()) {
        Log.error(String.format(ERROR_CODE + ERROR_MSG, a.get_SourcePositionStart()));
      }
    });
  }

}
