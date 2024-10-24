/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.MCGrammarSymbolTableHelper;
import de.monticore.grammar.grammar._ast.ASTNonTerminal;
import de.monticore.grammar.grammar._cocos.GrammarASTNonTerminalCoCo;
import de.monticore.grammar.grammar._symboltable.MCGrammarSymbol;
import de.monticore.grammar.grammar._symboltable.ProdSymbol;
import de.monticore.grammar.grammar._symboltable.RuleComponentSymbol;
import de.se_rwth.commons.StringTransformations;
import de.se_rwth.commons.logging.Log;

import java.util.List;
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
      RuleComponentSymbol componentSymbol = a.getSymbol();
      Optional<ProdSymbol> rule = MCGrammarSymbolTableHelper.getEnclosingRule(a);
      Optional<MCGrammarSymbol> grammarSymbol = MCGrammarSymbolTableHelper
              .getMCGrammarSymbol(a.getEnclosingScope());
      for (MCGrammarSymbol g : grammarSymbol.get().getSuperGrammarSymbols()) {
        Optional<ProdSymbol> ruleSymbol = g.getProd(rule.get().getName());
        if (ruleSymbol.isPresent()) {
          List<RuleComponentSymbol> rcs = ruleSymbol.get().getSpannedScope()
                  .resolveRuleComponentMany(attributename);
          if (!rcs.isEmpty()) {
            if (rcs.get(0).isIsTerminal()) {
              logError(rule.get(), attributename, componentSymbol, "production of a terminal", a);
            } else if (rcs.get(0).isIsConstantGroup()) {
              logError(rule.get(), attributename, componentSymbol, "production of a constant group", a);
            } else if (rcs.get(0).isIsConstant()) {
              logError(rule.get(), attributename, componentSymbol, "production of a constant", a);
            } else if (rcs.get(0).isIsLexerNonterminal()) {
              logError(rule.get(), attributename, componentSymbol, "production of a lexer nonterminal", a);
            } else if (rcs.get(0).isIsNonterminal() && rcs.get(0).getReferencedProd().isPresent()
                    && (rcs.get(0).getReferencedProd().get().isIsLexerProd() && componentSymbol.getReferencedProd().get().isIsLexerProd()) || !rcs.get(0).getReferencedProd().get().getName().equals(componentSymbol.getReferencedProd().get().getName())) {
              if (!(rcs.get(0).getReferencedProd().get().isIsLexerProd() && componentSymbol.getReferencedProd().get().isIsLexerProd())) {
                logError(rule.get(), attributename, componentSymbol, "nonterminal " + rcs.get(0).getReferencedProd().get().getName(), a);
              }
            }
          } else {
            //try to find NonTerminal with same Name, but with capitalised start -> will both become the same attribute
            rcs = ruleSymbol.get().getSpannedScope().resolveRuleComponentDownMany(StringTransformations.capitalize(attributename));
            if (!rcs.isEmpty() && rcs.get(0).isIsNonterminal() && rcs.get(0).getReferencedProd().isPresent()
                    && !rcs.get(0).getReferencedProd().get().getName().equals(componentSymbol.getReferencedProd().get().getName())) {
              // logs error when e.g. State = F; A extends State = f:R;
              // because F form State will evaluate to attributeName with small f
              logError(ruleSymbol.get(), attributename,
                      componentSymbol, "nonterminal " + rcs.get(0).getReferencedProd().get().getName(), a);
            }
          }
        }
      }
    }
  }

  protected void logError(ProdSymbol ruleSymbol, String attributename,
                        RuleComponentSymbol componentSymbol, String actualType, ASTNonTerminal a) {
    Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT,
        ruleSymbol.getName(),
        attributename,
        componentSymbol.getReferencedProd().get().getName(),
        actualType,
        a.get_SourcePositionStart()));
  }
}
