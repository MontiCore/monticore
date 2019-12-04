/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._cocos.GrammarASTMCGrammarCoCo;
import de.monticore.grammar.grammar._symboltable.MCGrammarSymbol;
import de.monticore.grammar.grammar._symboltable.ProdSymbol;
import de.se_rwth.commons.StringTransformations;
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
    MCGrammarSymbol grammarSymbol = a.getSymbol();

    if (!a.isComponent()) {
      List<ProdSymbol> externalProds = grammarSymbol.getProds().stream().
          filter(ProdSymbol::isIsExternal).collect(Collectors.toList());
      for(MCGrammarSymbol symbol: grammarSymbol.getAllSuperGrammars()){
        Collection<ProdSymbol> prodSymbols = symbol.getProds();
        for(ProdSymbol mcProdSymbol : prodSymbols){
          if (mcProdSymbol.isIsExternal()) {
            externalProds.add(mcProdSymbol);
          }
        }
      }

      List<ProdSymbol> prods = grammarSymbol.getProds().stream().
          filter(prodSymbol -> prodSymbol.isClass() || prodSymbol.isIsAbstract()).collect(Collectors.toList());
      for(MCGrammarSymbol symbol: grammarSymbol.getAllSuperGrammars()){
        Collection<ProdSymbol> prodSymbols = symbol.getProds();
        for(ProdSymbol mcProdSymbol : prodSymbols){
          if (mcProdSymbol.isIsAbstract() || mcProdSymbol.isClass()) {
            prods.add(mcProdSymbol);
          }
        }
      }

      if(!externalProds.isEmpty()) {
        for (ProdSymbol prodSymbol : prods) {
          for (int i = externalProds.size()-1; i >= 0; i--) {
            ProdSymbol externalProdSymbol = externalProds.get(i);
            if (prodSymbol.getName().equals(externalProdSymbol.getName())) {
              externalProds.remove(i);
            }
          }
        }
      }

      for (ProdSymbol prodSymbol: externalProds) {
        for (ProdSymbol prod : prods) {
          if (prod.getProdComponent(StringTransformations.uncapitalize(prodSymbol.getName())).isPresent()) {
              Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT, prodSymbol.getName()), a.get_SourcePositionStart());
          }
        }
      }
     }
  }

}

