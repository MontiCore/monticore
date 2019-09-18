/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.codegen.mc2cd.MCGrammarSymbolTableHelper;
import de.monticore.grammar.grammar._ast.ASTAbstractProd;
import de.monticore.grammar.grammar._ast.ASTProd;
import de.monticore.grammar.grammar._ast.ASTRuleReference;
import de.monticore.grammar.grammar._cocos.GrammarASTAbstractProdCoCo;
import de.monticore.grammar.grammar._cocos.GrammarASTProdCoCo;
import de.monticore.grammar.grammar._symboltable.ProdSymbol;
import de.se_rwth.commons.logging.Log;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Checks that prods do not inherit their symbols from more than one class
 */
public class InheritedSymbolProperty implements GrammarASTProdCoCo {

  public static final String ERROR_CODE = "0xA0125";

  public static final String ERROR_MSG_FORMAT = " The rule %s inherits symbols from more than one super class.";

  @Override
  public void check(ASTProd a) {
    ProdSymbol s = a.getProdSymbol();
    Set<ProdSymbol> superProds = MCGrammarSymbolTableHelper.getAllSuperProds(s);
    boolean found = false;
    for (ProdSymbol prod : superProds) {
      if (found && prod.isSymbolDefinition()) {
        Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT, a.getName()), a.get_SourcePositionStart());
      } else if (prod.isSymbolDefinition()) {
        found = true;
      }
    }
  }

}
