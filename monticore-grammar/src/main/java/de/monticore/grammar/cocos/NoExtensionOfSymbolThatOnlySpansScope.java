/* (c) https://github.com/MontiCore/monticore */
package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar._ast.ASTAbstractProd;
import de.monticore.grammar.grammar._ast.ASTClassProd;
import de.monticore.grammar.grammar._ast.ASTInterfaceProd;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._cocos.GrammarASTMCGrammarCoCo;
import de.monticore.grammar.grammar._symboltable.MCGrammarSymbol;
import de.monticore.grammar.grammar._symboltable.ProdSymbol;
import de.monticore.grammar.grammar._symboltable.ProdSymbolSurrogate;
import de.monticore.grammar.grammar._symboltable.ProdSymbolSurrogateBuilder;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcsimplegenerictypes.MCSimpleGenericTypesMill;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class NoExtensionOfSymbolThatOnlySpansScope implements GrammarASTMCGrammarCoCo {

  public static final String ERROR_CODE = "0xA0810";

  public static final String ERROR_MSG_FORMAT = " The production %s extends the symbol production %s and spans a scope " +
    "without being a symbol itself.";

  @Override
  public void check(ASTMCGrammar node) {
    MCGrammarSymbol symbol = node.getSymbol();
    Map<String, ProdSymbol> prodsWithInherited = symbol.getProdsWithInherited();
    Collection<ProdSymbol> prods = symbol.getProds();
    for(ProdSymbol prod: prods){
      if(prod.isIsScopeSpanning() && !prod.isIsSymbolDefinition()) {
        checkProd(prod, prod, prodsWithInherited);
      }
    }
  }

  protected void checkProd(ProdSymbol original, ProdSymbol prod, Map<String, ProdSymbol> prodsWithInherited){
    List<ProdSymbolSurrogate> superProds = new ArrayList<>(prod.getSuperProds());
    superProds.addAll(prod.getSuperInterfaceProds());
    checkProdsAndLogError(superProds, original, prod, prodsWithInherited);
  }

  protected void checkProdsAndLogError(List<ProdSymbolSurrogate> superProds, ProdSymbol original, ProdSymbol prod, Map<String, ProdSymbol> prodsWithInherited){
    for(ProdSymbolSurrogate surrogate: superProds){
      ProdSymbol superProd = prodsWithInherited.get(surrogate.getName());
      if(superProd.isIsSymbolDefinition()){
        logError(original, superProd);
      }
      checkProd(prod, superProd, prodsWithInherited);
    }
  }

  protected void logError(ProdSymbol original, ProdSymbol superProd){
    Log.error(ERROR_CODE + String.format(ERROR_MSG_FORMAT, original, superProd));
  }



}
