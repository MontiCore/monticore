// (c) https://github.com/MontiCore/monticore

/* (c) https://github.com/MontiCore/monticore */
package de.monticore.grammar;

import de.monticore.grammar.grammar._symboltable.MCGrammarSymbol;
import de.se_rwth.commons.logging.Log;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

// TODO Delete after release 7.2.0
@Deprecated
public class LexNamerFix extends LexNamer {

  private Map<String, String> usedLex = new HashMap<String, String>();

  private int lexCounter = 0;


  @Override
  public Set<String> getLexnames() {
    return usedLex.keySet();
  }

  @Override
  public String getLexName(MCGrammarSymbol grammarSymbol, String sym) {
    if (usedLex.containsKey(sym)) {
      return usedLex.get(sym);
    }

    String goodName = createGoodName(sym);
    if (goodName.isEmpty() || grammarSymbol.getProd(goodName).isPresent()) {
      goodName = "LEXNAME" + lexCounter++;
    }
    usedLex.put(sym, goodName);
    Log.debug("Using lexer symbol " + goodName + " for symbol '" + sym + "'", "LexNamer");
    return goodName;
  }
}
