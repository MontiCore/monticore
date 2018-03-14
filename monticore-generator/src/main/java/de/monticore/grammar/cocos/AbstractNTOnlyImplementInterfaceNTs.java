/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import java.util.List;
import java.util.Optional;

import de.monticore.grammar.grammar._ast.ASTAbstractProd;
import de.monticore.grammar.grammar._ast.ASTRuleReference;
import de.monticore.grammar.grammar._cocos.GrammarASTAbstractProdCoCo;
import de.monticore.grammar.symboltable.MCProdSymbol;
import de.se_rwth.commons.logging.Log;

/**
 * Checks that abstract nonterminals only implement interface nonterminals.
 *
 * @author KH
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
        Optional<MCProdSymbol> ruleSymbol = a.getEnclosingScope().get().resolve(i.getName(),
            MCProdSymbol.KIND);
        if (ruleSymbol.isPresent()) {
          MCProdSymbol r = ruleSymbol.get();
          if (!r.isInterface()) {
            Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT, a.getName(), r.getName()),
                a.get_SourcePositionStart());
          }
        }
      }
    }
  }
}
