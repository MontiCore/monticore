/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._cocos.GrammarASTMCGrammarCoCo;
import de.monticore.grammar.grammar._symboltable._symboltable.MCGrammarSymbol;
import de.monticore.grammar.grammar._symboltable._symboltable.MCProdSymbol;
import de.se_rwth.commons.logging.Log;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Checks that external nonterminals only occur in a component grammar.
 *

 */
public class ExternalNTOnlyInComponentGrammar implements GrammarASTMCGrammarCoCo {
  
  public static final String ERROR_CODE = "0xA0276";
  
  public static final String ERROR_MSG_FORMAT = " The external nonterminal %s must not be used in a grammar not marked " +
          "as a grammar component.";
  
  @Override
  public void check(ASTMCGrammar a) {
    MCGrammarSymbol grammarSymbol = (MCGrammarSymbol) a.getSymbol();

    if (!a.isComponent()) {
//      for (ASTProd p : a.getExternalProdList()) {
//        Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT, p.getName()),
//                a.get_SourcePositionStart());
//      }
      List<MCProdSymbol> externalProds = grammarSymbol.getProds().stream().
          filter(MCProdSymbol::isExternal).collect(Collectors.toList());
      for(MCGrammarSymbol symbol: grammarSymbol.getAllSuperGrammars()){
        Collection<MCProdSymbol> prodSymbols = symbol.getProds();
        for(MCProdSymbol mcProdSymbol : prodSymbols){
          if (mcProdSymbol.isExternal()) {
            externalProds.add(mcProdSymbol);
          }
        }
      }

      List<MCProdSymbol> prods = grammarSymbol.getProds().stream().
          filter(prodSymbol -> prodSymbol.isClass() || prodSymbol.isAbstract()).collect(Collectors.toList());
      for(MCGrammarSymbol symbol: grammarSymbol.getAllSuperGrammars()){
        Collection<MCProdSymbol> prodSymbols = symbol.getProds();
        for(MCProdSymbol mcProdSymbol : prodSymbols){
          if (mcProdSymbol.isAbstract() || mcProdSymbol.isClass()) {
            prods.add(mcProdSymbol);
          }
        }
      }

      if(!externalProds.isEmpty()) {
        for (MCProdSymbol prodSymbol : prods) {
          for (int i = externalProds.size()-1; i >= 0; i--) {
            MCProdSymbol externalProdSymbol = externalProds.get(i);
            if (prodSymbol.getName().equals(externalProdSymbol.getName())) {
              externalProds.remove(i);
            }
          }
        }
      }

      if(!externalProds.isEmpty()){
        Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT, externalProds.get(0).getName()), a.get_SourcePositionStart());
      }
    }
  }

}

