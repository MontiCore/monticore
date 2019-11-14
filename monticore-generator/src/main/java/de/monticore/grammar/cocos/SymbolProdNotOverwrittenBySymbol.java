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
 * and logs an error if both prods define a symbol
 * e.g. grammar A { symbol Foo; }  grammar B extends A { symbol Foo; }
 * only one of the prods can define a symbol
 * the symbol property is inherited from the super prod and does not need to be defined again
 */
public class SymbolProdNotOverwrittenBySymbol implements GrammarASTMCGrammarCoCo {

  public static final String ERROR_CODE = "0xA0274";

  public static final String ERROR_MSG_FORMAT = " The Prod %s from grammar %s is a symbol and overwritten by the prod %s of grammar %s that also defines a symbol." +
      "Remove the second symbol definition, because the symbol property is inherited anyway.";

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
            // log error if both prod define a symbol themselves
            if (superProd.get().isIsSymbolDefinition() && prod.isIsSymbolDefinition()) {
              Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT, superProd.get().getName(), superGrammar.getName(),
                  prod.getName(), grammarSymbol.getName()));
            }
          }
        }
      }
    }
  }
}
