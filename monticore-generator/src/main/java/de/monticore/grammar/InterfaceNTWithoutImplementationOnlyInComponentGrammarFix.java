/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar;

import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._cocos.GrammarASTMCGrammarCoCo;
import de.monticore.grammar.grammar._symboltable.MCGrammarSymbol;
import de.monticore.grammar.grammar._symboltable.ProdSymbol;
import de.monticore.grammar.grammar._symboltable.RuleComponentSymbol;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Checks that abstract nonterminals witheout extending productions only occur in a component grammar.
 */
public class InterfaceNTWithoutImplementationOnlyInComponentGrammarFix implements GrammarASTMCGrammarCoCo {

  public static final String ERROR_CODE = "0xA0278";

  public static final String ERROR_MSG_FORMAT = " The interface nonterminal %s must not be used without nonterminals " +
      "implementing it in a grammar not marked as a grammar component.";

  @Override
  public void check(ASTMCGrammar a) {
    MCGrammarSymbol grammarSymbol = a.getSymbol();

    // if the given grammar is a component grammar we ignore it, since there we allow interfaces without an
    // implementation
    if (!a.isComponent()) {

      // we collect all interface productions from the grammar and all its super grammars and save them to the list
      // interfaceProds
      List<ProdSymbol> interfaceProds = grammarSymbol.getProds().stream().
          filter(ProdSymbol::isIsInterface).collect(Collectors.toList());
      for (MCGrammarSymbol symbol : grammarSymbol.getAllSuperGrammars()) {
        Collection<ProdSymbol> prodSymbols = symbol.getProds();
        for (ProdSymbol mcProdSymbol : prodSymbols) {
          if (mcProdSymbol.isIsInterface()) {
            interfaceProds.add(mcProdSymbol);
          }
        }
      }

      // for every interface production we get all other interfaces implementing it and save it to map of all interface
      // productions mapping to all their subinterfaces
      Map<String, List<ProdSymbol>> subSymbols = new HashMap<>();
      for (ProdSymbol interfaceProd : interfaceProds) {
        for (ProdSymbol superInterface : interfaceProd.getSuperInterfaceProds()) {
          if (!subSymbols.containsKey(superInterface.getName())) {
            subSymbols.put(superInterface.getName(), new ArrayList<>());
          }
          subSymbols.get(superInterface.getName()).add(interfaceProd);
        }
      }

      // we collect all non-interface productions from the grammar and all its super-grammars and save them to the list
      // prods
      List<ProdSymbol> prods = grammarSymbol.getProds().stream().
          filter(prodSymbol -> prodSymbol.isClass() || prodSymbol.isIsAbstract()).collect(Collectors.toList());
      for (MCGrammarSymbol symbol : grammarSymbol.getAllSuperGrammars()) {
        Collection<ProdSymbol> prodSymbols = symbol.getProds();
        for (ProdSymbol mcProdSymbol : prodSymbols) {
          if (mcProdSymbol.isIsAbstract() || mcProdSymbol.isClass()) {
            prods.add(mcProdSymbol);
          }
        }
      }

      // from the list interfaceProds we remove every interface production that does not get used in a non-interface
      // production
      if (!interfaceProds.isEmpty()) {
        List<ProdSymbol> temp = new ArrayList<>();
        for (ProdSymbol prod : prods) {
          for (RuleComponentSymbol component : prod.getProdComponents()) {
            for (ProdSymbol interfaceProd : interfaceProds) {
              if (component.isPresentReferencedType() && component.getReferencedType().equals(interfaceProd.getName())) {
                temp.add(interfaceProd);
              }
            }
          }
        }
        interfaceProds = temp;
      }

      // for every remaining interface production we check if it or any subinterface gets implemented anywhere and
      // otherwise log an error since it gets used in a production without getting implemented
      for (ProdSymbol interf : interfaceProds) {
        if (!isImplemented(interf, prods, subSymbols)) {
          Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT, interf.getName()), a.get_SourcePositionStart());
        }
      }
    }
  }

  protected boolean isImplemented(ProdSymbol interfaceSymbol, List<ProdSymbol> prods, Map<String, List<ProdSymbol>> subSymbols) {

    // if a production exists that directly implements the interface return true
    for (ProdSymbol prod : prods) {
      for (ProdSymbol implemented : prod.getSuperInterfaceProds()) {
        if (implemented.getName().equals(interfaceSymbol.getName())) {
          return true;
        }
      }
    }

    // otherwise get all interfaces that extend this interface and recursively call this method on them. if any of them
    // return true end the method and return true. otherwise, if none of its subinterface gets implemented, return false
    if (subSymbols.containsKey(interfaceSymbol.getName())) {
      for (ProdSymbol subSymbol : subSymbols.get(interfaceSymbol.getName())) {
        if (isImplemented(subSymbol, prods, subSymbols)) {
          return true;
        }
      }
    }

    return false;
  }

}
