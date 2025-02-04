/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import java.util.List;
import java.util.Optional;

import de.monticore.grammar.grammar._ast.ASTClassProd;
import de.monticore.grammar.grammar._ast.ASTRuleReference;
import de.monticore.grammar.grammar._cocos.GrammarASTClassProdCoCo;
import de.monticore.grammar.grammar._symboltable.ProdSymbol;
import de.se_rwth.commons.logging.Log;

/**
 * Checks that nonterminals only implement interface nonterminals.
 *

 */
public class NTOnlyImplementInterfaceNTs implements GrammarASTClassProdCoCo {
  
  public static final String ERROR_CODE = "0xA2102";
  
  public static final String ERROR_MSG_FORMAT = " The nonterminal %s must not implement the nonterminal %s. "
      +
      "Nonterminals may only implement interface nonterminals.";
  
  @Override
  public void check(ASTClassProd a) {
    if (!a.getSuperInterfaceRuleList().isEmpty()) {
      List<ASTRuleReference> interfaces = a.getSuperInterfaceRuleList();
      for (ASTRuleReference i : interfaces) {
        Optional<ProdSymbol> ruleSymbol = a.getEnclosingScope().resolveProd(i.getName());
        if (ruleSymbol.isPresent()) {
          ProdSymbol r = ruleSymbol.get();
          if (!r.isIsInterface()) {
            Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT, a.getName(), r.getName()),
                a.get_SourcePositionStart());
          }
        }
      }
    }
  }
}
