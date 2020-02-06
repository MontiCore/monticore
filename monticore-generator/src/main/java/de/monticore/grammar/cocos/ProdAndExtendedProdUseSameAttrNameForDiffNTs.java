/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.codegen.mc2cd.MCGrammarSymbolTableHelper;
import de.monticore.grammar.grammar._ast.ASTClassProd;
import de.monticore.grammar.grammar._ast.ASTNonTerminal;
import de.monticore.grammar.grammar._ast.ASTRuleReference;
import de.monticore.grammar.grammar._cocos.GrammarASTNonTerminalCoCo;
import de.monticore.grammar.grammar._symboltable.ProdSymbol;
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
      Optional<ProdSymbol> rule = MCGrammarSymbolTableHelper.getEnclosingRule(a);
      if (rule.isPresent() && rule.get().getAstNode() instanceof ASTClassProd) {
        ASTClassProd prod = (ASTClassProd) rule.get().getAstNode();
        if (!prod.getSuperRuleList().isEmpty()) {
          ASTRuleReference type = prod.getSuperRuleList().get(0);
          String typename = type.getTypeName();
          Optional<ProdSymbol> ruleSymbol = type.getEnclosingScope().getEnclosingScope()
              .resolveProd(typename);
          if (ruleSymbol.isPresent()) {
            List<RuleComponentSymbol> componentSymbolList = ruleSymbol.get().getSpannedScope()
                .resolveRuleComponentMany(attributename);
            if (!componentSymbolList.isEmpty()) {
              for (RuleComponentSymbol symbol : componentSymbolList) {
                if (symbol.isIsLexerNonterminal()) {
                  logError(prod, ruleSymbol.get(), attributename, componentSymbol,
                      "production that is a lexical nonTerminal", a);
                } else if (symbol.isIsConstant()) {
                  logError(prod, ruleSymbol.get(), attributename, componentSymbol,
                      "production that is not a constant", a);
                } else if (symbol.isIsConstantGroup()) {
                  logError(prod, ruleSymbol.get(), attributename, componentSymbol,
                      "production that is not a constant group", a);
                } else if (symbol.isIsTerminal()) {
                  logError(prod, ruleSymbol.get(), attributename, componentSymbol,
                      "production that is a terminal named " + symbol.getName(), a);
                } else if (symbol.isIsNonterminal() && symbol.getReferencedProd().isPresent()
                    && !symbol.getReferencedProd().get().getName().equals(componentSymbol.getReferencedProd().get().getName())) {
                  logError(prod, ruleSymbol.get(), attributename,
                      componentSymbol, "nonterminal " + symbol.getReferencedProd().get().getName(), a);
                }
              }
            } else {
              //try to find NonTerminal with same Name, but with capitalised start -> will both become the same attribute
              componentSymbolList = ruleSymbol.get().getSpannedScope().resolveRuleComponentMany(StringTransformations.capitalize(attributename));
              for (RuleComponentSymbol ruleComponentSymbol : componentSymbolList) {
                if (ruleComponentSymbol.isIsNonterminal() && ruleComponentSymbol.getReferencedProd().isPresent()
                    && !ruleComponentSymbol.getReferencedProd().get().getName().equals(componentSymbol.getReferencedProd().get().getName())) {
                  // logs error when e.g. State = F; A extends State = f:R;
                  // because F form State will evaluate to attributeName with small f
                  logError(prod, ruleSymbol.get(), attributename,
                      componentSymbol, "nonterminal " + ruleComponentSymbol.getReferencedProd().get().getName(), a);
                }
              }
            }
          }
        }
      }
    }
  }

  private void logError(ASTClassProd prod, ProdSymbol ruleSymbol, String attributename,
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
