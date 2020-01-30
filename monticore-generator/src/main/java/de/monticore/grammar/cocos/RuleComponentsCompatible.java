package de.monticore.grammar.cocos;

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
public class RuleComponentsCompatible implements GrammarASTProdCoCo {

  public static final String ERROR_CODE = "0xA4090";

  public static final String ERROR_MSG_FORMAT = " The prod: '%s' contains different rule components with the same name: " +
      "'%s' with incompatible types: '%s' and '%s'.";

  private static final String TERMINAL = "Terminal";

  private static final String NON_TERMINAL = "NonTerminal";

  @Override
  public void check(ASTProd node) {
    ProdSymbol symbol = node.getSymbol();
    outer:
    for (int i = 0; i < symbol.getProdComponents().size(); i++) {
      for (int j = i + 1; j < symbol.getProdComponents().size(); j++) {
        if (symbol.getProdComponents().get(i).getName().equals(symbol.getProdComponents().get(j).getName())) {
          boolean compatible = areTypesCompatible(symbol.getProdComponents().get(i), symbol.getProdComponents().get(j), node.getName());
          if (!compatible) {
            break outer;
          }
        }
      }
    }
  }

  private String getRuleComponentType(RuleComponentSymbol symbol) {
    if (symbol.isIsTerminal()) {
      return TERMINAL;
    } else if (symbol.isIsNonterminal()) {
      return NON_TERMINAL;
    }
    return "";
  }

  private boolean areTypesCompatible(RuleComponentSymbol firstSymbol, RuleComponentSymbol secondSymbol,
                                     String prodName) {
    String firstType = getRuleComponentType(firstSymbol);
    String secondType = getRuleComponentType(secondSymbol);
    if (!firstType.equals(secondType)) {
      // one Terminal and one NonTerminal
      LogError(prodName, firstSymbol.getName(), firstType, secondType);
      return false;
    } else if (firstType.equals(NON_TERMINAL) && !firstSymbol.getReferencedType().equals(secondSymbol.getReferencedType())) {
      // two different NonTerminals
      LogError(prodName, firstSymbol.getName(), firstSymbol.getReferencedType(), secondSymbol.getReferencedType());
      return false;
    }
    return true;
  }

  private void LogError(String prodName, String ruleCompName, String firstType, String secondType) {
    Log.error(ERROR_CODE + String.format(ERROR_MSG_FORMAT, prodName, ruleCompName, firstType, secondType));
  }
}
