/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar._ast.ASTSymbolRule;
import de.monticore.grammar.grammar._cocos.GrammarASTSymbolRuleCoCo;
import de.monticore.grammar.grammar._symboltable.ProdSymbol;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;

public class SymbolRuleWithoutSymbolRef implements GrammarASTSymbolRuleCoCo {
  
  public static final String ERROR_CODE = "0xA0117";
  
  public static final String ERROR_MSG_FORMAT = " There is no symbol defining rule that belongs to symbolrule %s";
  
  @Override
  public void check(ASTSymbolRule a) {
    Optional<ProdSymbol> symbol = a.getEnclosingScope().resolveProd(a.getType());
    if (!symbol.isPresent() || !symbol.get().isSymbolDefinition()) {
      Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT, a.getType()),
              a.get_SourcePositionStart());
    }
  }

}
