/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import java.util.List;
import java.util.Optional;

import de.monticore.grammar.grammar._ast.ASTLexProd;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._cocos.GrammarASTMCGrammarCoCo;
import de.monticore.grammar.symboltable.MCGrammarSymbol;
import de.monticore.grammar.symboltable.MCProdSymbol;
import de.se_rwth.commons.logging.Log;

/**
 * Checks that nonterminals or only overridden by normal nonterminals.
 *
 * @author KH
 */
public class OverridingLexNTs implements GrammarASTMCGrammarCoCo {
  
  public static final String ERROR_CODE = "0xA4026";

  public static final String ERROR_MSG_FORMAT = " The lexical production %s must not use a different type to "
      + "store the token than the overridden production.";

  @Override
  public void check(ASTMCGrammar a) {
    MCGrammarSymbol grammarSymbol = (MCGrammarSymbol) a.getSymbol().get();
    List<MCGrammarSymbol> grammarSymbols = grammarSymbol.getSuperGrammarSymbols();
    
    for (MCGrammarSymbol s : grammarSymbols) {
      for (ASTLexProd p : a.getLexProdList()) {
        doCheck(s.getProdWithInherited(p.getName()), p);
      }
    }
  }
  
  private void doCheck(Optional<MCProdSymbol> prodSymbol, ASTLexProd lexProd) {
    if (prodSymbol.isPresent() && prodSymbol.get().isLexerProd()
        && !((ASTLexProd) prodSymbol.get().getAstNode().get()).getTypeList()
            .equals(lexProd.getTypeList())) {
      Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT, lexProd.getName()));
    }
  }
  
}
