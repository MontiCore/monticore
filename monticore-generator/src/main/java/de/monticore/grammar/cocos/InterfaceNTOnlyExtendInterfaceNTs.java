/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import java.util.List;
import java.util.Optional;

import de.monticore.grammar.grammar._ast.ASTInterfaceProd;
import de.monticore.grammar.grammar._ast.ASTRuleReference;
import de.monticore.grammar.grammar._cocos.GrammarASTInterfaceProdCoCo;
import de.monticore.grammar.symboltable.MCProdSymbol;
import de.se_rwth.commons.logging.Log;

/**
 * Checks that nonterminals only extends abstract or normal nonterminals..
 *
 * @author KH
 */
public class InterfaceNTOnlyExtendInterfaceNTs implements GrammarASTInterfaceProdCoCo {
  
  public static final String ERROR_CODE = "0xA2116";
  
  public static final String ERROR_MSG_FORMAT = " The interface nonterminal %s must not extend the%s nonterminal %s. "
      +
      "Interface nonterminals may only extend interface nonterminals.";
  
  @Override
  public void check(ASTInterfaceProd a) {
    if (!a.getSuperInterfaceRuleList().isEmpty()) {
      List<ASTRuleReference> superRules = a.getSuperInterfaceRuleList();
      for (ASTRuleReference sr : superRules) {
        Optional<MCProdSymbol> ruleSymbol = a.getEnclosingScope().get().resolve(sr.getName(),
            MCProdSymbol.KIND);
        if (ruleSymbol.isPresent()) {
          MCProdSymbol r = ruleSymbol.get();
          boolean isAbstract = r.isAbstract();
          boolean isExternal = r.isExternal();
          if (!r.isInterface()) {
            Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT, a.getName(),
                isAbstract ? " abstract" : isExternal ? " external" : "", r.getName()),
                a.get_SourcePositionStart());
          }
        }
      }
    }
  }
  
}
