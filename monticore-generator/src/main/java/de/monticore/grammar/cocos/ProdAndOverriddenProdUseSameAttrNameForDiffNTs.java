/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import java.util.Optional;

import de.monticore.codegen.mc2cd.MCGrammarSymbolTableHelper;
import de.monticore.grammar.grammar._ast.ASTNonTerminal;
import de.monticore.grammar.grammar._cocos.GrammarASTNonTerminalCoCo;
import de.monticore.grammar.grammar._symboltable.MCGrammarSymbol;
import de.monticore.grammar.grammar._symboltable.RuleComponentSymbol;
import de.monticore.grammar.grammar._symboltable.ProdSymbol;
import de.se_rwth.commons.logging.Log;

/**
 * Checks that an attribute name is not used twice for different nonterminals.
 *

 */
public class ProdAndOverriddenProdUseSameAttrNameForDiffNTs implements GrammarASTNonTerminalCoCo {
  
  public static final String ERROR_CODE = "0xA4025";
  
  public static final String ERROR_MSG_FORMAT = " The overriding production %s must not use " +
      "the name %s for the nonterminal %s as the overridden production uses this name for the nonterminal %s";
      
  @Override
  public void check(ASTNonTerminal a) {
    if (a.isPresentUsageName()) {
      String attributename = a.getUsageName();
      Optional<RuleComponentSymbol> componentSymbol = a.getEnclosingScope()
          .resolve(attributename, RuleComponentSymbol.KIND);
      if (!componentSymbol.isPresent()) {
        Log.error("0xA1124 ASTNonterminal " + a.getName() + " couldn't be resolved.");
      }
      Optional<ProdSymbol> rule = MCGrammarSymbolTableHelper.getEnclosingRule(a);
      if (!rule.isPresent()) {
        Log.error("0xA1125 Symbol for enclosing produktion of the component " + a.getName()
            + " couldn't be resolved.");
      }
      Optional<MCGrammarSymbol> grammarSymbol = MCGrammarSymbolTableHelper
          .getMCGrammarSymbol(a);
      if (!grammarSymbol.isPresent()) {
        Log.error(
            "0xA1126 grammar symbol for the component " + a.getName() + " couldn't be resolved.");
      }
      for (MCGrammarSymbol g : grammarSymbol.get().getSuperGrammarSymbols()) {
        Optional<ProdSymbol> ruleSymbol = g.getProd(rule.get().getName());
        if (ruleSymbol.isPresent()) {
          Optional<RuleComponentSymbol> rcs = ruleSymbol.get().getSpannedScope()
              .resolve(attributename, RuleComponentSymbol.KIND);
          if (rcs.isPresent() && !rcs.get().getReferencedProd().get().getName()
              .equals(componentSymbol.get().getReferencedProd().get().getName())) {
            Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT,
                rule.get().getName(),
                attributename,
                componentSymbol.get().getReferencedProd().get().getName(),
                rcs.get().getReferencedProd().get().getName()),
                a.get_SourcePositionStart());
          }
        }
      }
    }
  }
}
