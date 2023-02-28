/* (c) https://github.com/MontiCore/monticore */
package de.monticore.grammar;

import de.monticore.grammar.grammar._ast.ASTProd;
import de.monticore.grammar.grammar._cocos.GrammarASTProdCoCo;
import de.monticore.grammar.grammar._symboltable.ProdSymbol;
import de.monticore.grammar.grammar._symboltable.RuleComponentSymbol;
import de.se_rwth.commons.logging.Log;

/**
 * checks for each prod if there exist RuleComponents which have the same usageName but do not have compatible types
 * not compatible if:
 * -> one NonTerminal and one Terminal
 * -> two different NonTerminal types
 */
// TODO Delete after release 7.5.0
@Deprecated
public class RuleComponentsCompatibleFix implements GrammarASTProdCoCo {

  public static final String ERROR_CODE = "0xA4090";

  public static final String ERROR_MSG_FORMAT = " The prod: '%s' contains different rule components with the same name: " +
      "'%s' with incompatible types.";

  @Override
  public void check(ASTProd node) {
    ProdSymbol symbol = node.getSymbol();
    outer:
    for (int i = 0; i < symbol.getProdComponents().size(); i++) {
      for (int j = i + 1; j < symbol.getProdComponents().size(); j++) {
        if (!symbol.getProdComponents().get(i).getName().isEmpty() && symbol.getProdComponents().get(i).getName().equals(symbol.getProdComponents().get(j).getName())) {
          boolean compatible = areTypesCompatible(symbol.getProdComponents().get(i), symbol.getProdComponents().get(j), node.getName());
          if (!compatible) {
            break outer;
          }
        }
      }
    }
  }

  protected boolean areTypesCompatible(RuleComponentSymbol firstSymbol, RuleComponentSymbol secondSymbol,
                                     String prodName) {
    if (firstSymbol.isIsTerminal()) {
      if (secondSymbol.isIsTerminal() || (secondSymbol.isIsNonterminal() && secondSymbol.getReferencedProd().get().isIsLexerProd())) {
        return true;
      }
      logError(prodName, firstSymbol.getName());
      return false;
    } else if (firstSymbol.isIsConstantGroup()) {
      if (!secondSymbol.isIsConstantGroup()) {
        logError(prodName, firstSymbol.getName());
      }
      return false;
    } else if (firstSymbol.isIsConstant()) {
      if (!secondSymbol.isIsConstant()) {
        logError(prodName, firstSymbol.getName());
      }
      return false;
    } else if (secondSymbol.isIsTerminal()) {
      if (firstSymbol.isIsNonterminal() && firstSymbol.getReferencedProd().get().isIsLexerProd()) {
        return true;
      }
      logError(prodName, firstSymbol.getName());
      return false;
    } else if (secondSymbol.isIsConstant() || secondSymbol.isIsConstantGroup()) {
      logError(prodName, firstSymbol.getName());
      return false;
    } else if (!firstSymbol.getReferencedType().equals(secondSymbol.getReferencedType())) {
      // two different NonTerminals
      if (!(firstSymbol.getReferencedProd().get().isIsLexerProd() && secondSymbol.getReferencedProd().get().isIsLexerProd())) {
        logError(prodName, firstSymbol.getName());
        return false;
      }
    }
    return true;
  }

  protected void logError(String prodName, String ruleCompName) {
    Log.error(ERROR_CODE + String.format(ERROR_MSG_FORMAT, prodName, ruleCompName));
  }
}
