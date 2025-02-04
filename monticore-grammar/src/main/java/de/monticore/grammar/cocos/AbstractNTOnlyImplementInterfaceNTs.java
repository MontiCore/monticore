/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import java.util.List;
import java.util.Optional;

import de.monticore.grammar.grammar._ast.ASTAbstractProd;
import de.monticore.grammar.grammar._ast.ASTRuleReference;
import de.monticore.grammar.grammar._cocos.GrammarASTAbstractProdCoCo;
import de.monticore.grammar.grammar._symboltable.ProdSymbol;
import de.se_rwth.commons.logging.Log;

/**
 * Checks that abstract nonterminals only implement interface nonterminals.
 *
 */
public class AbstractNTOnlyImplementInterfaceNTs implements GrammarASTAbstractProdCoCo {
  
  public static final String ERROR_CODE = "0xA2106";
  
  public static final String ERROR_MSG_FORMAT = " The abstract nonterminal %s must not implement the nonterminal %s. "
      +
      "Abstract nonterminals may only implement interface nonterminals.";
  
  @Override
  public void check(ASTAbstractProd a) {
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
