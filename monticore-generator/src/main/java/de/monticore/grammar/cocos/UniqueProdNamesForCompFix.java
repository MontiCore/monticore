/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._cocos.GrammarASTMCGrammarCoCo;
import de.monticore.grammar.grammar._symboltable.MCGrammarSymbol;
import de.monticore.grammar.grammar._symboltable.ProdSymbol;
import de.se_rwth.commons.logging.Log;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Checks that productions are unique in a composition of grammars.
 *

 */
public class UniqueProdNamesForCompFix implements GrammarASTMCGrammarCoCo {
  
  public static final String ERROR_CODE = "0xA0144";
  
  public static final String ERROR_MSG_FORMAT = " The nonterminal %s is inherited from conflicting grammars: %s.";
  
  @Override
  public void check(ASTMCGrammar a) {
    MCGrammarSymbol grammarSymbol = a.getSymbol();
    Map<String, Set<MCGrammarSymbol>> symbolsInGrammars = new HashMap<>();
    // Collect all productions defined in super grammars
    for (MCGrammarSymbol superGrammar : grammarSymbol.getAllSuperGrammars()) {
      for (ProdSymbol ps : superGrammar.getProds()) {
        symbolsInGrammars.computeIfAbsent(ps.getName(), s1 -> new HashSet<>()).add(superGrammar);
      }
    }
    // and in the grammar itself
    for (ProdSymbol ps : grammarSymbol.getProds()) {
      symbolsInGrammars.computeIfAbsent(ps.getName(), s1 -> new HashSet<>()).add(grammarSymbol);
    }

    for (Map.Entry<String, Set<MCGrammarSymbol>> e : symbolsInGrammars.entrySet()) {
      // Remove overriden production false-positives
      for (MCGrammarSymbol g : new HashSet<>(e.getValue())) {
        e.getValue().removeAll(g.getAllSuperGrammars());
      }
    }
    // symbolsInGrammars should now contain the grammars that define a production
    // Overriden productions are mapped to the overriding grammar
    for (Map.Entry<String, Set<MCGrammarSymbol>> e : symbolsInGrammars.entrySet()) {
      // Fail CoCo in case a prod is defined in more than one location
      if (e.getValue().size() > 1) {
        Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT, e.getKey(),
                        e.getValue().stream()
                                .map(MCGrammarSymbol::getFullName)
                                .sorted()
                                .collect(Collectors.joining(", "))),
                a.get_SourcePositionStart());
      }
    }
  }
}
