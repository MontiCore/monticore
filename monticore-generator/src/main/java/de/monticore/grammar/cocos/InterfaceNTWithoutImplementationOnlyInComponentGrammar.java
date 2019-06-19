/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import java.util.*;
import java.util.stream.Collectors;

import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._cocos.GrammarASTMCGrammarCoCo;
import de.monticore.grammar.grammar._symboltable.MCGrammarSymbol;
import de.monticore.grammar.grammar._symboltable.MCProdSymbol;
import de.monticore.grammar.grammar._symboltable.MCProdSymbolReference;
import de.se_rwth.commons.logging.Log;

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
    MCGrammarSymbol grammarSymbol = (MCGrammarSymbol) a.getSymbol();

    if (!a.isComponent()) {
//      for (ASTProd p : a.getInterfaceProdList()) {
//        boolean extensionFound = false;
//        entryLoop:
//        for (Map.Entry<String, MCProdSymbol> entry : grammarSymbol.getProdsWithInherited().entrySet()) {
//          MCProdSymbol rs = (MCProdSymbol) entry.getValue();
//          // TODO GV: getAllSuperInterfaces()?
//          for (MCProdSymbolReference typeSymbol : rs.getSuperInterfaceProds()) {
//            if (p.getName().equals(typeSymbol.getName())) {
//              extensionFound = true;
//              break entryLoop;
//            }
//          }
//        }
//        if (!extensionFound) {
//          Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT, p.getName()), a.get_SourcePositionStart());
//        }
//      }
      List<MCProdSymbol> interfaceProds = grammarSymbol.getProds().stream().
          filter(MCProdSymbol::isInterface).collect(Collectors.toList());
      for(MCGrammarSymbol symbol: grammarSymbol.getAllSuperGrammars()){
        Collection<MCProdSymbol> prodSymbols = symbol.getProds();
        for(MCProdSymbol mcProdSymbol : prodSymbols){
          if (mcProdSymbol.isInterface()) {
            interfaceProds.add(mcProdSymbol);
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

      if(!interfaceProds.isEmpty()) {
        List<MCProdSymbol> temp = new ArrayList<>(interfaceProds);
        for(MCProdSymbol interfaceProdSymbol : interfaceProds){
          for(MCProdSymbolReference interfaceProdExtended : interfaceProdSymbol.getSuperInterfaceProds()){
            for(int i = interfaceProds.size()-1;i>=0;--i){
              MCProdSymbol interfaceProd = interfaceProds.get(i);
              if(interfaceProdExtended.getReferencedSymbol().getName().equals(interfaceProd.getName())){
                temp.remove(interfaceProdExtended.getReferencedSymbol());
              }
            }
          }
        }
        interfaceProds = temp;

//        Iterator<MCProdSymbol> iter = interfaceProds.iterator();
//        while(iter.hasNext()){
//          MCProdSymbol prodSymbol = iter.next();
//          if(!prodSymbol.getSuperInterfaceProds().isEmpty()){
//            for(MCProdSymbolReference interfaceProdExtended : prodSymbol.getSuperInterfaceProds()) {
//              for (int i = interfaceProds.size() - 1; i >= 0; --i) {
//                Iterator<MCProdSymbol> interfaceProdIter = interfaceProds.iterator();
//              }
//            }
//          }
//        }
      }
        
      if(!interfaceProds.isEmpty()){
        for (MCProdSymbol prodSymbol : prods) {
          for (MCProdSymbolReference interfaceProdImplemented : prodSymbol.getSuperInterfaceProds()) {
            for (int i = interfaceProds.size() - 1; i >= 0; --i) {
              MCProdSymbol interfaceProd = interfaceProds.get(i);
              if (interfaceProdImplemented.getName().equals(interfaceProd.getName())) {
                interfaceProds.remove(i);
              }
            }
          }
        }
      }

      if(!interfaceProds.isEmpty()){
        Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT, interfaceProds.get(0).getName()), a.get_SourcePositionStart());
      }
    }


  }

}
