/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar._ast.ASTProd;
import de.monticore.grammar.grammar._cocos.GrammarASTProdCoCo;
import de.monticore.grammar.grammar._symboltable.ProdSymbolSurrogate;
import de.se_rwth.commons.logging.Log;

public class ProdExtendsNotExistingProd implements GrammarASTProdCoCo {

  public static final String ERROR_CODE = "0xA0113";

  public static final String ERROR_MSG_FORMAT = " The production %s extends or implements the non-existent production %s";

  @Override
  public void check(ASTProd node) {
    for(ProdSymbolSurrogate loader: node.getSymbol().getSuperProds()){
      if(!node.getEnclosingScope().resolveProd(loader.getName()).isPresent()){
        logError(node.getName(), loader.getName());
      }
    }

    for(ProdSymbolSurrogate loader: node.getSymbol().getSuperInterfaceProds()){
      if(!node.getEnclosingScope().resolveProd(loader.getName()).isPresent()){
        logError(node.getName(), loader.getName());
      }
    }
  }

  public void logError(String name, String undefinedName){
    Log.error(String.format(ERROR_CODE+ERROR_MSG_FORMAT,name, undefinedName));
  }
}
