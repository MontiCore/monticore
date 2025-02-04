/* (c) https://github.com/MontiCore/monticore */
package de.monticore.grammar.cocos;


import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._cocos.GrammarASTMCGrammarCoCo;
import de.monticore.grammar.grammar._symboltable.MCGrammarSymbol;
import de.monticore.grammar.grammar._symboltable.ProdSymbol;
import de.se_rwth.commons.logging.Log;

import java.util.List;
import java.util.stream.Collectors;

/**
 * checks whether a grammar contains two or more prods that have the same name.
 * e.g. grammar A { B = "b"; B = "a";} is not allowed
 * prod names must be unique within a grammar
 */

public class UniqueProdNameInGrammar implements GrammarASTMCGrammarCoCo {

  public static final String ERROR_CODE = "0xA0112";

  public static final String ERROR_MSG_FORMAT = " Grammar '%s' contains two productions named '%s'. Production names must be unique within a grammar.";

  @Override
  public void check(ASTMCGrammar node) {
    MCGrammarSymbol grammarSymbol = node.getSymbol();
    List<String> prodNames = grammarSymbol.getProds()
        .stream()
        .map(ProdSymbol::getName)
        .collect(Collectors.toList());
    for (int i = 0; i < prodNames.size(); i++) {
      for (int j = i + 1; j < prodNames.size(); j++) {
        if (prodNames.get(i).equals(prodNames.get(j))) {
          Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT, node.getName(), prodNames.get(i)));
        }
      }
    }
  }
}
