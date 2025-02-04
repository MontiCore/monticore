/* (c) https://github.com/MontiCore/monticore */
package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._cocos.GrammarASTMCGrammarCoCo;
import de.monticore.grammar.grammar._symboltable.MCGrammarSymbol;
import de.monticore.grammar.grammar._symboltable.ProdSymbol;
import de.monticore.grammar.grammar._symboltable.ProdSymbolSurrogate;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class NoExtensionOfSymbolThatOnlySpansScope implements GrammarASTMCGrammarCoCo {

  public static final String ERROR_CODE = "0xA0810";

  public static final String ERROR_MSG_FORMAT = " The production %s extends the symbol production %s and spans a scope " +
          "without being a symbol itself.";

  protected boolean hasSymbol;
  protected boolean hasScope;
  protected ProdSymbol symbolProd;

  @Override
  public void check(ASTMCGrammar node) {
    MCGrammarSymbol symbol = node.getSymbol();
    Map<String, ProdSymbol> prodsWithInherited = symbol.getProdsWithInherited();
    Collection<ProdSymbol> prods = symbol.getProds();
    for(ProdSymbol prod: prods){
      hasSymbol = false;
      hasScope = false;
      if(prod.isIsScopeSpanning() && !prod.isIsSymbolDefinition()) {
        checkProd(prod, prodsWithInherited);
        if (hasSymbol && !hasScope) {
          logError(prod, symbolProd);
        }
      }
    }
  }

  protected void checkProd(ProdSymbol prod, Map<String, ProdSymbol> prodsWithInherited){
    List<ProdSymbolSurrogate> superProds = new ArrayList<>(prod.getSuperProds());
    superProds.addAll(prod.getSuperInterfaceProds());
    checkProdsAndLogError(superProds, prod, prodsWithInherited);
  }

  protected void checkProdsAndLogError(List<ProdSymbolSurrogate> superProds, ProdSymbol prod, Map<String, ProdSymbol> prodsWithInherited){
    for(ProdSymbolSurrogate surrogate: superProds){
      ProdSymbol superProd = prodsWithInherited.get(surrogate.getName());
      if(superProd.isIsSymbolDefinition()){
        hasSymbol = true;
        symbolProd = superProd;
      }
      if (superProd.isIsScopeSpanning()) {
        hasScope = true;
      }
      checkProd(superProd, prodsWithInherited);
    }
  }

  protected void logError(ProdSymbol original, ProdSymbol superProd){
    Log.error(ERROR_CODE + String.format(ERROR_MSG_FORMAT, original, superProd));
  }



}
