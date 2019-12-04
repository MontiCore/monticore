/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import com.google.common.collect.Lists;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._cocos.GrammarASTMCGrammarCoCo;
import de.monticore.grammar.grammar._symboltable.MCGrammarSymbol;
import de.monticore.grammar.grammar._symboltable.ProdSymbol;
import de.monticore.grammar.grammar._symboltable.ProdSymbolLoader;
import de.se_rwth.commons.StringTransformations;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Checks that abstract nonterminals witheout extending productions only occur in a component grammar.
 *
 */
public class AbstractNTWithoutExtensionOnlyInComponentGrammar implements GrammarASTMCGrammarCoCo {

  public static final String ERROR_CODE = "0xA0277";

  public static final String ERROR_MSG_FORMAT = " The abstract nonterminal %s must not be used without nonterminals " +
          "extending it in a grammar not marked as a grammar component.";

  @Override
  public void check(ASTMCGrammar a) {
    MCGrammarSymbol grammarSymbol = a.getSymbol();

    if (!a.isComponent()) {
      List<ProdSymbol> abstractProds = grammarSymbol.getProds().stream().
              filter(ProdSymbol::isIsAbstract).collect(Collectors.toList());
      for(MCGrammarSymbol symbol: grammarSymbol.getAllSuperGrammars()){
        Collection<ProdSymbol> prodSymbols = symbol.getProds();
        for(ProdSymbol mcProdSymbol : prodSymbols){
          if (mcProdSymbol.isIsAbstract()) {
            abstractProds.add(mcProdSymbol);
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

      if(!abstractProds.isEmpty()) {
        List<ProdSymbol> temp = new ArrayList<>(abstractProds);
        for(ProdSymbol abstractProdSymbol : abstractProds){
          for(ProdSymbolLoader absractProdExtended : abstractProdSymbol.getSuperProds()){
            for(int i = abstractProds.size()-1;i>=0;--i){
              ProdSymbol abstractProd = abstractProds.get(i);
              if(absractProdExtended.getLoadedSymbol().getName().equals(abstractProd.getName())){
                temp.remove(absractProdExtended.getLoadedSymbol());
              }
            }
          }
        }
        abstractProds = temp;
      }

      if(!abstractProds.isEmpty()){
        for (ProdSymbol prodSymbol : prods) {
          for (ProdSymbolLoader abstractProdImplemented : prodSymbol.getSuperProds()) {
            for (int i = abstractProds.size() - 1; i >= 0; --i) {
              ProdSymbol interfaceProd = abstractProds.get(i);
              if (abstractProdImplemented.getName().equals(interfaceProd.getName())) {
                abstractProds.remove(i);
              }
            }
          }
        }
      }

      for (ProdSymbol prodSymbol: abstractProds) {
        for (ProdSymbol prod : prods) {
          List<String> checkList = Lists.newArrayList(prodSymbol.getName());
          prodSymbol.getSuperProds().stream().forEach(i -> checkList.add(i.getName()));
          for (String name: checkList) {
            if (prod.getProdComponent(StringTransformations.uncapitalize(name)).isPresent()) {
              Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT, name), a.get_SourcePositionStart());
            }
          }
        }
      }

    }
  }


}
