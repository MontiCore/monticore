/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.codegen.mc2cd.MCGrammarSymbolTableHelper;
import de.monticore.grammar.grammar._ast.ASTNonTerminal;
import de.monticore.grammar.grammar._cocos.GrammarASTNonTerminalCoCo;
import de.monticore.grammar.grammar._symboltable.MCGrammarSymbol;
import de.monticore.grammar.grammar._symboltable.ProdSymbol;
import de.monticore.grammar.grammar._symboltable.RuleComponentSymbol;
import de.se_rwth.commons.StringTransformations;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;

/**
 * Checks that an attribute name is not used twice for different nonterminals.
 */
public class ProdAndOverriddenProdUseSameAttrNameForDiffNTs implements GrammarASTNonTerminalCoCo {

  public static final String ERROR_CODE = "0xA4025";

  public static final String ERROR_MSG_FORMAT = " The overriding production %s must not use " +
      "the name %s for the nonterminal %s as the overridden production uses this name for the %s";

  @Override
  public void check(ASTNonTerminal a) {
    if (a.isPresentUsageName()) {
      String attributename = a.getUsageName();
      Optional<RuleComponentSymbol> componentSymbol = a.getEnclosingScope()
          .resolveRuleComponentLocally(attributename);
      if (!componentSymbol.isPresent()) {
        Log.error("0xA1124 ASTNonterminal " + a.getName() + " couldn't be resolved.");
      }
      Optional<ProdSymbol> rule = MCGrammarSymbolTableHelper.getEnclosingRule(a);
      if (!rule.isPresent()) {
        Log.error("0xA1125 Symbol for enclosing produktion of the component " + a.getName()
            + " couldn't be resolved.");
      }
      Optional<MCGrammarSymbol> grammarSymbol = MCGrammarSymbolTableHelper
          .getMCGrammarSymbol(a.getEnclosingScope());
      if (!grammarSymbol.isPresent()) {
        Log.error(
            "0xA1126 grammar symbol for the component " + a.getName() + " couldn't be resolved.");
      }
      for (MCGrammarSymbol g : grammarSymbol.get().getSuperGrammarSymbols()) {
        Optional<ProdSymbol> ruleSymbol = g.getProd(rule.get().getName());
        if (ruleSymbol.isPresent()) {
          Optional<RuleComponentSymbol> rcs = ruleSymbol.get().getSpannedScope()
              .resolveRuleComponentLocally(attributename);
          if (rcs.isPresent()) {
            if (rcs.get().isTerminal() && !"".equals(rcs.get().getUsageName())) {
              logError(rule.get(), attributename, componentSymbol.get(), "production of a terminal", a);
            } else if (rcs.get().isConstantGroup()) {
              logError(rule.get(), attributename, componentSymbol.get(), "production of a constant group", a);
            } else if (rcs.get().isConstant()) {
              logError(rule.get(), attributename, componentSymbol.get(), "production of a constant", a);
            } else if (rcs.get().isLexerNonterminal()) {
              logError(rule.get(), attributename, componentSymbol.get(), "production of a lexer nonterminal", a);
            } else if (rcs.get().isNonterminal() && rcs.get().getReferencedProd().isPresent()
                && !rcs.get().getReferencedProd().get().getName().equals(componentSymbol.get().getReferencedProd().get().getName())) {
              logError(rule.get(), attributename, componentSymbol.get(), "nonterminal " + rcs.get().getReferencedProd().get().getName(), a);
            }
          }else {
            //try to find NonTerminal with same Name, but with capitalised start -> will both become the same attribute
            rcs = ruleSymbol.get().getSpannedScope().resolveRuleComponent(StringTransformations.capitalize(attributename));
            if (rcs.isPresent() && rcs.get().isNonterminal() && rcs.get().getReferencedProd().isPresent()
                && !rcs.get().getReferencedProd().get().getName().equals(componentSymbol.get().getReferencedProd().get().getName())) {
              // logs error when e.g. State = F; A extends State = f:R;
              // because F form State will evaluate to attributeName with small f
              logError(ruleSymbol.get(), attributename,
                  componentSymbol.get(), "nonterminal " + rcs.get().getReferencedProd().get().getName(), a);
            }
          }
        }
      }
    }
  }

  private void logError(ProdSymbol ruleSymbol, String attributename,
                        RuleComponentSymbol componentSymbol, String actualType, ASTNonTerminal a) {
    Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT,
        ruleSymbol.getName(),
        attributename,
        componentSymbol.getReferencedProd().get().getName(),
        actualType,
        a.get_SourcePositionStart()));
  }
}
