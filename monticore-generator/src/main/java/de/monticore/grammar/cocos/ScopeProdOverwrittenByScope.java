// (c) https://github.com/MontiCore/monticore
package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._cocos.GrammarASTMCGrammarCoCo;
import de.monticore.grammar.grammar._symboltable.MCGrammarSymbol;
import de.monticore.grammar.grammar._symboltable.ProdSymbol;
import de.se_rwth.commons.logging.Log;

import java.util.List;
import java.util.Optional;

/**
 * checks if a prod is overwriting a prod of a super grammar
 * and logs an error if both prods define a scope
 * e.g. grammar A { scope Foo; }  grammar B extends A { scope Foo; }
 * only one of the prods can should a scope
 * the scope property is inherited from the super prod and does not need to be defined again
 */
public class ScopeProdOverwrittenByScope implements GrammarASTMCGrammarCoCo {

  public static final String ERROR_CODE = "0xA0275";

  public static final String ERROR_MSG_FORMAT = " The Prod %s from grammar %s is a scope and overwritten by the prod %s of grammar %s that also defines a scope." +
      "Remove the second scope definition, because the scope property is inherited anyway.";

  @Override
  public void check(ASTMCGrammar node) {
    MCGrammarSymbol grammarSymbol = node.getSymbol();
    // get all super grammars
    List<MCGrammarSymbol> superGrammarSymbols = grammarSymbol.getSuperGrammarSymbols();
    for (MCGrammarSymbol superGrammar : superGrammarSymbols) {
      // for every prod of the current grammar
      for (ProdSymbol prod : grammarSymbol.getProds()) {
        // enums cannot define symbols
        if (!prod.isIsEnum()) {
          // finds a prod with the same name in the super grammar if one is present
          Optional<ProdSymbol> superProd = superGrammar.getProd(prod.getName());
          if (superProd.isPresent()) {
            // log error if both prod define a scope themselves
            if (superProd.get().isIsScopeSpanning() && prod.isIsScopeSpanning()) {
              Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT, superProd.get().getName(), superGrammar.getName(),
                  prod.getName(), grammarSymbol.getName()));
            }
          }
        }
      }
    }
  }
}