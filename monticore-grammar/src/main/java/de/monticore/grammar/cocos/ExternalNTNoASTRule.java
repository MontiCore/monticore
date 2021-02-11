/* (c) https://github.com/MontiCore/monticore */
package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar._ast.ASTASTRule;
import de.monticore.grammar.grammar._cocos.GrammarASTASTRuleCoCo;
import de.monticore.grammar.grammar._symboltable.ProdSymbol;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;

public class ExternalNTNoASTRule implements GrammarASTASTRuleCoCo {

  public static final String ERROR_CODE = "0xA4118";

  public static final String ERROR_MSG_FORMAT = " The external production %s must not have a " +
      "corresponding ASTRule.";

  @Override
  public void check(ASTASTRule node) {
    Optional<ProdSymbol> prod = node.getEnclosingScope().resolveProd(node.getType());
    if(prod.isPresent() && prod.get().isIsExternal()){
      Log.error(ERROR_CODE+String.format(ERROR_MSG_FORMAT, prod.get().getName()));
    }
  }
}
