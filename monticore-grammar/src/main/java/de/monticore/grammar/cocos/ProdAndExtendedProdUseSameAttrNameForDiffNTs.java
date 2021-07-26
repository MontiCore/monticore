/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import com.google.common.collect.Lists;
import de.monticore.grammar.MCGrammarSymbolTableHelper;
import de.monticore.grammar.grammar._ast.ASTNonTerminal;
import de.monticore.grammar.grammar._ast.ASTProd;
import de.monticore.grammar.grammar._cocos.GrammarASTNonTerminalCoCo;
import de.monticore.grammar.grammar._symboltable.ProdSymbol;
import de.monticore.grammar.grammar._symboltable.ProdSymbolSurrogate;
import de.monticore.grammar.grammar._symboltable.RuleComponentSymbol;
import de.se_rwth.commons.StringTransformations;
import de.se_rwth.commons.logging.Log;

import java.util.List;
import java.util.Optional;

/**
 * Checks that an attribute name is not used twice for different nonterminals.
 */
public class ProdAndExtendedProdUseSameAttrNameForDiffNTs implements GrammarASTNonTerminalCoCo {

  public static final String ERROR_CODE = "0xA4024";

  public static final String ERROR_MSG_FORMAT = " The production %s extending the production %s must not use the\n"
          +
          "name %s for the nonterminal %s as %s already uses this name for the %s.";

  @Override
  public void check(ASTNonTerminal a) {
    if (a.isPresentUsageName() && a.isPresentSymbol()) {
      String attributename = a.getUsageName();
      RuleComponentSymbol componentSymbol = a.getSymbol();
      Optional<ProdSymbol> optProd = MCGrammarSymbolTableHelper.getEnclosingRule(a);
      if (optProd.isPresent() && optProd.get().isClass()) {
        ProdSymbol prod = optProd.get();
        for (ProdSymbolSurrogate s : getAllSuperProds(prod)) {
          ProdSymbol superprod = s.lazyLoadDelegate();
          List<RuleComponentSymbol> componentSymbolList = superprod.getSpannedScope().resolveRuleComponentDownMany(attributename);
          if (!componentSymbolList.isEmpty()) {
            for (RuleComponentSymbol symbol : componentSymbolList) {
              if (symbol.isIsLexerNonterminal()) {
                logError(prod.getAstNode(), superprod, attributename, componentSymbol,
                        "production that is a lexical nonTerminal", a);
              } else if (symbol.isIsConstant()) {
                logError(prod.getAstNode(), superprod, attributename, componentSymbol,
                        "production that is not a constant", a);
              } else if (symbol.isIsConstantGroup()) {
                logError(prod.getAstNode(), superprod, attributename, componentSymbol,
                        "production that is not a constant group", a);
              } else if (symbol.isIsTerminal()) {
                logError(prod.getAstNode(), superprod, attributename, componentSymbol,
                        "production that is a terminal named " + symbol.getName(), a);
              } else if (symbol.isIsNonterminal() && symbol.getReferencedProd().isPresent()
                      && !symbol.getReferencedProd().get().getName().equals(componentSymbol.getReferencedProd().get().getName())) {
                logError(prod.getAstNode(), superprod, attributename,
                        componentSymbol, "nonterminal " + symbol.getReferencedProd().get().getName(), a);
              }
            }
          } else {
            //try to find NonTerminal with same Name, but with capitalised start -> will both become the same attribute
            componentSymbolList = superprod.getSpannedScope().resolveRuleComponentDownMany(StringTransformations.capitalize(attributename));
            for (RuleComponentSymbol ruleComponentSymbol : componentSymbolList) {
              if (ruleComponentSymbol.isIsNonterminal() && ruleComponentSymbol.getReferencedProd().isPresent()
                      && !ruleComponentSymbol.getReferencedProd().get().getName().equals(componentSymbol.getReferencedProd().get().getName())) {
                // logs error when e.g. State = F; A extends State = f:R;
                // because F form State will evaluate to attributeName with small f
                logError(prod.getAstNode(), superprod, attributename,
                        componentSymbol, "nonterminal " + ruleComponentSymbol.getReferencedProd().get().getName(), a);
              }
            }
          }
        }
      }
    }
  }

  protected List<ProdSymbolSurrogate> getAllSuperProds(ProdSymbol prod) {
    List<ProdSymbolSurrogate> ret = Lists.newArrayList(prod.getSuperProds());
    for (ProdSymbolSurrogate surrogate: prod.getSuperProds()) {
      ProdSymbol superProd = surrogate.lazyLoadDelegate();
      ret.addAll(getAllSuperProds(superProd));
    }
    return ret;
  }

  protected void logError(ASTProd prod, ProdSymbol ruleSymbol, String attributename,
                        RuleComponentSymbol componentSymbol, String actualType, ASTNonTerminal a) {
    Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT,
            prod.getName(),
            ruleSymbol.getName(),
            attributename,
            componentSymbol.getReferencedProd().get().getName(),
            ruleSymbol.getName(),
            actualType,
            a.get_SourcePositionStart()));
  }
}
