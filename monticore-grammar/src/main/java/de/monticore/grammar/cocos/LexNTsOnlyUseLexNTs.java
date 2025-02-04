/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import java.util.Optional;

import de.monticore.grammar.MCGrammarSymbolTableHelper;
import de.monticore.grammar.grammar._ast.ASTLexNonTerminal;
import de.monticore.grammar.grammar._cocos.GrammarASTLexNonTerminalCoCo;
import de.monticore.grammar.grammar._symboltable.MCGrammarSymbol;
import de.monticore.grammar.grammar._symboltable.ProdSymbol;
import de.se_rwth.commons.logging.Log;

/**
 * Checks that used nonterminals are lexical nonterminals.
 *
 */
public class LexNTsOnlyUseLexNTs implements GrammarASTLexNonTerminalCoCo {

  public static final String ERROR_CODE = "0xA4017";

  public static final String ERROR_MSG_FORMAT = " The lexical production %s must not use" +
          " the nonterminal %s because %s is defined by a production of" +
          " another type than lexical. Lexical productions may only reference nonterminals" +
          " defined by lexical productions.";

  @Override
  public void check(ASTLexNonTerminal a) {
    Optional<MCGrammarSymbol> grammarSymbol = MCGrammarSymbolTableHelper
        .getMCGrammarSymbol(a.getEnclosingScope());
    
    Optional<ProdSymbol> ruleSymbol = MCGrammarSymbolTableHelper.getEnclosingRule(a);
    String ruleName = ruleSymbol.isPresent() ? ruleSymbol.get().getName() : "";
    if (grammarSymbol.isPresent()
        && grammarSymbol.get().getProdWithInherited(a.getName()).isPresent() &&
        !grammarSymbol.get().getProdWithInherited(a.getName()).get().isIsLexerProd()) {
      Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT, ruleName, a.getName(), a.getName()),
          a.get_SourcePositionStart());
    }
  }
}
