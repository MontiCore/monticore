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

public class NoForbiddenProdNameAddon implements GrammarASTMCGrammarCoCo {

  public static final String ERROR_CODE = "0xA4120";

  public static final String ERROR_MSG_FORMAT = " There must not exist a production with the name %s in the grammar %s if there is already a production with the name %s.";

  protected static final String BUILDER = "Builder";

  @Override
  public void check(ASTMCGrammar node) {
    String grammarName = node.getName();
    MCGrammarSymbol symbol = node.getSymbol();
    Collection<ProdSymbol> prods = symbol.getProdsWithInherited().values();
    for(ProdSymbol prod:prods){
      String prodName = prod.getName();
      if(prodName.endsWith(BUILDER)){
        handle(grammarName, BUILDER, prodName, prods);
      }
    }
  }

  protected void handle(String grammarName, String addon, String prodName, Collection<ProdSymbol> prods){
    String prodNameWithoutAddon = prodName.substring(0, prodName.lastIndexOf(addon));
    List<ProdSymbol> forbidden = prods.stream()
        .filter(p -> p.getName().equals(prodNameWithoutAddon))
        .collect(Collectors.toList());

    if(forbidden.size()>0){
      for(ProdSymbol prod: forbidden){
        Log.error(ERROR_CODE + String.format(ERROR_MSG_FORMAT, prodName, grammarName, prod.getName()));
      }
    }
  }
}
