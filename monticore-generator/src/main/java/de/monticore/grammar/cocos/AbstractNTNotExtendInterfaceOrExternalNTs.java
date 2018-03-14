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
 * Checks that abstract nonterminals only extends abstract or normal nonterminals..
 *
 * @author KH
 */
public class AbstractNTNotExtendInterfaceOrExternalNTs implements GrammarASTAbstractProdCoCo {
  
  public static final String ERROR_CODE = "0xA2107";
  
  public static final String ERROR_MSG_FORMAT = " The abstract nonterminal %s must not extend the %s nonterminal %s. " +
                                      "Abstract nonterminals may only extend abstract or normal nonterminals.";
  
  @Override
  public void check(ASTAbstractProd a) {
    if (!a.getSuperRuleList().isEmpty()) {
      List<ASTRuleReference> superRules = a.getSuperRuleList();
      for(ASTRuleReference sr : superRules){
        Optional<MCProdSymbol> ruleSymbol = a.getEnclosingScope().get().resolve(sr.getName(), MCProdSymbol.KIND);
        if(ruleSymbol.isPresent()){
          MCProdSymbol r = ruleSymbol.get();
          boolean isInterface = r.isInterface();
          boolean isExternal =  r.isExternal();
          if(isInterface || isExternal){
            Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT, a.getName(), isInterface? "interface": "external", r.getName()),
                    a.get_SourcePositionStart());
          }
        }
      }
    }
  }

}
