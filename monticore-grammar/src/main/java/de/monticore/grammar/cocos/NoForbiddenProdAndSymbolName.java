/* (c) https://github.com/MontiCore/monticore */
package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._cocos.GrammarASTMCGrammarCoCo;
import de.monticore.grammar.grammar._symboltable.MCGrammarSymbol;
import de.monticore.grammar.grammar._symboltable.ProdSymbol;
import de.se_rwth.commons.logging.Log;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class NoForbiddenProdAndSymbolName implements GrammarASTMCGrammarCoCo {

  public static final String ERROR_CODE = "0xA4122";

  public static final String ERROR_MSG_FORMAT = " There must not exist a production with the name %s in the grammar %s if " +
      "there already exists a symbol with the name %s.";

  public static final String SYMBOL = "Symbol";

  @Override
  public void check(ASTMCGrammar node) {
    String grammarName = node.getName();
    MCGrammarSymbol symbol = node.getSymbol();
    Collection<ProdSymbol> prods = symbol.getProdsWithInherited().values();
    List<ProdSymbol> symbolProds = prods.stream().filter(ProdSymbol::isIsSymbolDefinition).collect(Collectors.toList());
    for(ProdSymbol prod:prods){
      String prodName = prod.getName();
      if(prodName.endsWith(SYMBOL)){
        handle(grammarName, SYMBOL, prodName, symbolProds);
      }
    }
  }

  protected void handle(String grammarName, String addon, String prodName, Collection<ProdSymbol> prods){
    String prodNameWithoutAddon = prodName.substring(0, prodName.lastIndexOf(addon));
    List<ProdSymbol> forbidden = prods.stream()
        .filter(p -> p.getName().equals(prodNameWithoutAddon))
        .collect(Collectors.toList());

    if(!forbidden.isEmpty()){
      for(ProdSymbol prod: forbidden){
        Log.error(ERROR_CODE + String.format(ERROR_MSG_FORMAT, prodName, grammarName, prod.getName()));
      }
    }
  }
}
