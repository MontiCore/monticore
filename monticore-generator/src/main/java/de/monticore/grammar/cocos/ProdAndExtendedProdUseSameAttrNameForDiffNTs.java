/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import java.util.Optional;

import de.monticore.codegen.mc2cd.MCGrammarSymbolTableHelper;
import de.monticore.grammar.grammar._ast.ASTClassProd;
import de.monticore.grammar.grammar._ast.ASTNonTerminal;
import de.monticore.grammar.grammar._ast.ASTRuleReference;
import de.monticore.grammar.grammar._cocos.GrammarASTNonTerminalCoCo;
import de.monticore.grammar.grammar._symboltable.RuleComponentSymbol;
import de.monticore.grammar.grammar._symboltable.ProdSymbol;
import de.se_rwth.commons.logging.Log;

/**
 * Checks that an attribute name is not used twice for different nonterminals.
 *
 */
public class ProdAndExtendedProdUseSameAttrNameForDiffNTs implements GrammarASTNonTerminalCoCo {
  
  public static final String ERROR_CODE = "0xA4024";
  
  public static final String ERROR_MSG_FORMAT = " The production %s extending the production %s must not use the\n"
      +
      "name %s for the nonterminal %s as %s already uses this name for the nonterminal %s.";
  
  @Override
  public void check(ASTNonTerminal a) {
    if (a.isPresentUsageName()) {
      String attributename = a.getUsageName();
      Optional<RuleComponentSymbol> componentSymbol = a.getEnclosingScope2()
          .resolveRuleComponent(attributename);
      if (componentSymbol.isPresent()) {
        Optional<ProdSymbol> rule = MCGrammarSymbolTableHelper.getEnclosingRule(a);
        if (rule.isPresent() && rule.get().getAstNode().get() instanceof ASTClassProd) {
          ASTClassProd prod = (ASTClassProd) rule.get().getAstNode().get();
          if (!prod.getSuperRuleList().isEmpty()) {
            ASTRuleReference type = prod.getSuperRuleList().get(0);
            String typename = type.getTypeName();
            Optional<ProdSymbol> ruleSymbol = type.getEnclosingScope2().getEnclosingScope()
                .get().resolveProd(typename);
            if (ruleSymbol.isPresent()) {
              Optional<RuleComponentSymbol> rcs = ruleSymbol.get().getSpannedScope()
                  .resolveRuleComponent(attributename);
              if (rcs.isPresent() && !rcs.get().getReferencedProd().get().getName()
                  .equals(componentSymbol.get().getReferencedProd().get().getName())) {
                Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT,
                    prod.getName(),
                    ruleSymbol.get().getName(),
                    attributename,
                    componentSymbol.get().getReferencedProd().get().getName(),
                    ruleSymbol.get().getName(),
                    rcs.get().getReferencedProd().get().getName()),
                    a.get_SourcePositionStart());
              }
            }
          }
        }
      }
    }
  }
}
