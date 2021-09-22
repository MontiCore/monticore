/* (c) https://github.com/MontiCore/monticore */
package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._cocos.GrammarASTMCGrammarCoCo;
import de.monticore.grammar.grammar._symboltable.MCGrammarSymbol;
import de.monticore.grammar.grammar._symboltable.ProdSymbol;
import de.se_rwth.commons.logging.Log;

import java.util.List;
import java.util.stream.Collectors;

public class NoForbiddenSymbolNameAddon implements GrammarASTMCGrammarCoCo {

  public static final String ERROR_CODE = "0xA4121";

  public static final String ERROR_MSG_FORMAT = " There must not exist a symbol production with the name %s in the grammar %s if there is already a symbol production %s.";

  protected static final String MANY = "Many";

  protected static final String DOWN = "Down";

  protected static final String LOCALLY = "Locally";

  protected static final String ADAPTED = "Adapted";

  @Override
  public void check(ASTMCGrammar node) {
    String grammarName = node.getName();
    MCGrammarSymbol symbol = node.getSymbol();
    List<ProdSymbol> symbolProds = symbol.getProdsWithInherited().values()
        .stream()
        .filter(ProdSymbol::isIsSymbolDefinition)
        .collect(Collectors.toList());
    for(ProdSymbol prod: symbolProds){
      String prodName = prod.getName();
      if(prodName.endsWith(MANY)){
        handle(grammarName, MANY, prodName, symbolProds);
      }else if(prodName.endsWith(DOWN)) {
        handle(grammarName, DOWN, prodName, symbolProds);
      }else if(prodName.endsWith(LOCALLY)){
        handle(grammarName, LOCALLY, prodName, symbolProds);
      }else if(prodName.startsWith(ADAPTED)){
        handle(grammarName, ADAPTED, prodName, symbolProds);
      }
    }
  }

  protected void handle(String grammarName, String addon, String prodName, List<ProdSymbol> potentialSymbolProds){
    String prodNameWithoutAddon;
    if(addon.equals(ADAPTED)){
      prodNameWithoutAddon = prodName.substring(addon.length());
    }else{
      prodNameWithoutAddon = prodName.substring(0, prodName.lastIndexOf(addon));
    }
    List<ProdSymbol> forbidden = potentialSymbolProds.stream()
        .filter(p -> p.getName().equals(prodNameWithoutAddon))
        .collect(Collectors.toList());

    if(!forbidden.isEmpty()){
      for(ProdSymbol prod: forbidden){
        Log.error(ERROR_CODE + String.format(ERROR_MSG_FORMAT, prodName, grammarName, prod.getName()));
      }
    }
  }
}
