/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import com.google.common.collect.Lists;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._cocos.GrammarASTMCGrammarCoCo;
import de.monticore.grammar.grammar._symboltable.MCGrammarSymbol;
import de.monticore.grammar.grammar._symboltable.ProdSymbol;
import de.monticore.grammar.grammar._symboltable.ProdSymbolReference;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Checks that abstract nonterminals witheout extending productions only occur in a component grammar.
 *

 */
public class InterfaceNTWithoutImplementationOnlyInComponentGrammar implements GrammarASTMCGrammarCoCo {

  public static final String ERROR_CODE = "0xA0278";

  public static final String ERROR_MSG_FORMAT = " The interface nonterminal %s must not be used without nonterminals " +
          "implementing it in a grammar not marked as a grammar component.";

  @Override
  public void check(ASTMCGrammar a) {
    MCGrammarSymbol grammarSymbol = a.getMCGrammarSymbol();

    if (!a.isComponent()) {
      List<ProdSymbol> interfaceProds = grammarSymbol.getProds().stream().
          filter(ProdSymbol::isInterface).collect(Collectors.toList());
      for(MCGrammarSymbol symbol: grammarSymbol.getAllSuperGrammars()){
        Collection<ProdSymbol> prodSymbols = symbol.getProds();
        for(ProdSymbol mcProdSymbol : prodSymbols){
          if (mcProdSymbol.isInterface()) {
            interfaceProds.add(mcProdSymbol);
          }
        }
      }

      List<ProdSymbol> prods = grammarSymbol.getProds().stream().
          filter(prodSymbol -> prodSymbol.isClass() || prodSymbol.isAbstract()).collect(Collectors.toList());
      for(MCGrammarSymbol symbol: grammarSymbol.getAllSuperGrammars()){
        Collection<ProdSymbol> prodSymbols = symbol.getProds();
        for(ProdSymbol mcProdSymbol : prodSymbols){
          if (mcProdSymbol.isAbstract() || mcProdSymbol.isClass()) {
            prods.add(mcProdSymbol);
          }
        }
      }

      if(!interfaceProds.isEmpty()) {
        List<ProdSymbol> temp = new ArrayList<>(interfaceProds);
        for(ProdSymbol interfaceProdSymbol : interfaceProds){
          for(ProdSymbolReference interfaceProdExtended : interfaceProdSymbol.getSuperInterfaceProds()){
            for(int i = interfaceProds.size()-1;i>=0;--i){
              ProdSymbol interfaceProd = interfaceProds.get(i);
              if(interfaceProdExtended.getReferencedSymbol().getName().equals(interfaceProd.getName())){
                temp.remove(interfaceProdExtended.getReferencedSymbol());
              }
            }
          }
        }
        interfaceProds = temp;
      }
        
      if(!interfaceProds.isEmpty()){
        for (ProdSymbol prodSymbol : prods) {
          for (ProdSymbolReference interfaceProdImplemented : prodSymbol.getSuperInterfaceProds()) {
            for (int i = interfaceProds.size() - 1; i >= 0; --i) {
              ProdSymbol interfaceProd = interfaceProds.get(i);
              if (interfaceProdImplemented.getName().equals(interfaceProd.getName())) {
                interfaceProds.remove(i);
              }
            }
          }
        }
      }

      for (ProdSymbol interf: interfaceProds) {
        for (ProdSymbol prod : prods) {
          List<String> checkList = Lists.newArrayList(interf.getName());
          interf.getSuperInterfaceProds().stream().forEach(i -> checkList.add(i.getName()));
          for (String name: checkList) {
            if (prod.getProdComponent(name).isPresent()) {
              Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT, name), a.get_SourcePositionStart());
            }
          }
        }
      }

    }
  }

}
