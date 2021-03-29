/* (c) https://github.com/MontiCore/monticore */
package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._cocos.GrammarASTMCGrammarCoCo;
import de.monticore.grammar.grammar._symboltable.MCGrammarSymbol;
import de.monticore.grammar.grammar._symboltable.ProdSymbol;
import de.se_rwth.commons.logging.Log;

import java.util.List;
import java.util.stream.Collectors;

public class NoForbiddenSymbolName implements GrammarASTMCGrammarCoCo {

  public static final String ERROR_CODE = "0xA4099";

  public static final String ERROR_MSG_FORMAT = " There must not exist a symbol production with the name %s in the grammar %s.";

  protected static final String SYMBOL = "Symbol";

  @Override
  public void check(ASTMCGrammar node) {
    String grammarName = node.getName();
    MCGrammarSymbol symbol = node.getSymbol();
    List<ProdSymbol> symbolProds = symbol.getProdsWithInherited().values()
        .stream()
        .filter(ProdSymbol::isIsSymbolDefinition)
        .collect(Collectors.toList());
    if(grammarName.endsWith(SYMBOL)){
      String nameWithoutSymbol = grammarName.substring(0,grammarName.lastIndexOf(SYMBOL));
      List<ProdSymbol> forbidden = symbolProds.stream()
          .filter(p -> p.getName().equals(nameWithoutSymbol))
          .collect(Collectors.toList());
      if(forbidden.size()>0){
        for(ProdSymbol prod: forbidden){
          Log.error(ERROR_CODE + String.format(ERROR_MSG_FORMAT, prod.getName(), grammarName));
        }
      }
    }
  }

}
