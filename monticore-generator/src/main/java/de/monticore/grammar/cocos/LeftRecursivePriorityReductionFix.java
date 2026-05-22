/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar._ast.ASTClassProd;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._ast.ASTRuleReference;
import de.monticore.grammar.grammar._cocos.GrammarASTClassProdCoCo;
import de.monticore.grammar.grammar._symboltable.MCGrammarSymbol;
import de.monticore.grammar.grammar._symboltable.ProdSymbol;
import de.se_rwth.commons.logging.Log;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

// TODO: Remove once 7.9.0 is released

/**
 * Our Grammar2ANTLR does not support the reducing of priorities:
 * <a href="https://git.rwth-aachen.de/monticore/monticore/-/work_items/4473">issue</a>
 */
public class LeftRecursivePriorityReductionFix implements GrammarASTClassProdCoCo {

  public static final String ERROR_CODE = "0xA0143";

  public static final String ERROR_MSG_FORMAT = " Reducing the priority of the (indirect) left-recursive production `%s` using override is not supported.";

  protected MCGrammarSymbol grammarSymbol;

  @Override
  public void visit(ASTMCGrammar node) {
    grammarSymbol = node.getSymbol();
  }

  @Override
  public void check(ASTClassProd prod) {
    if (prod.getSuperInterfaceRuleList().isEmpty()) {
      return;
    }
    if (prod.getSymbol().isIsIndirectLeftRecursive() || prod.getSymbol().isIsDirectLeftRecursive()) {
      Optional<ProdSymbol> typeSymbol = grammarSymbol.getInheritedProd(prod.getName());
      if (typeSymbol.isPresent() && typeSymbol.get().isPresentAstNode() && typeSymbol.get()
              .getAstNode() instanceof ASTClassProd) {
        Map<String, Integer> priorityMap = new HashMap<>();
        for (ASTRuleReference rule : prod.getSuperInterfaceRuleList()) {
          if (rule.isPresentPrio()) {
            priorityMap.put(rule.getName(), Integer.parseInt(rule.getPrio()));
          } else {
            priorityMap.put(rule.getName(), 0);
          }
        }
        for (ASTRuleReference ruleInOverridden : ((ASTClassProd) typeSymbol.get()
                .getAstNode()).getSuperInterfaceRuleList()) {
          if (ruleInOverridden.isPresentPrio() && priorityMap.containsKey(ruleInOverridden.getName())) {
            if (priorityMap.get(ruleInOverridden.getName()) < Integer.parseInt(ruleInOverridden.getPrio())) {
              Log.error(ERROR_CODE + ERROR_MSG_FORMAT.formatted(ruleInOverridden.getName()),
                        prod.get_SourcePositionStart());
            }
          }
        }
      }
    }
  }

}
