/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import java.util.List;
import java.util.Optional;

import de.monticore.grammar.MCGrammarSymbolTableHelper;
import de.monticore.grammar.grammar._ast.ASTAbstractProd;
import de.monticore.grammar.grammar._cocos.GrammarASTAbstractProdCoCo;
import de.monticore.grammar.grammar._symboltable.MCGrammarSymbol;
import de.monticore.types.mcsimplegenerictypes.MCSimpleGenericTypesMill;
import de.se_rwth.commons.logging.Log;

/**
 * Checks that overriding abstract nonterminals do not have super rules or classes.
 *
 */
public class OverridingAbstractNTsHaveNoSuperRules implements GrammarASTAbstractProdCoCo {
  
  public static final String ERROR_CODE = "0xA4002";
  
  public static final String ERROR_MSG_FORMAT = " The abstract production %s overriding a production of " +
          "a sub grammar must not extend the production %s.\n" +
          "Hint: Overriding productions can only implement interfaces.";
  
  @Override
  public void check(ASTAbstractProd a) {
    Optional<MCGrammarSymbol> grammarSymbol = MCGrammarSymbolTableHelper
        .getMCGrammarSymbol(a.getEnclosingScope());
    List<MCGrammarSymbol> grammarSymbols = grammarSymbol.get().getSuperGrammarSymbols();
    
    if (!a.getSuperRuleList().isEmpty() || !a.getASTSuperClassList().isEmpty()) {
      String extendedType;
      if (!a.getSuperRuleList().isEmpty()){
        extendedType = a.getSuperRuleList().get(0).getName();
      }
      else{
        extendedType = MCSimpleGenericTypesMill
            .prettyPrint(a.getASTSuperClassList().get(0), false).trim();
      }
      
      for (MCGrammarSymbol s : grammarSymbols) {
        if (s.getProd(a.getName()).isPresent()) {
          Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT, a.getName(), extendedType),
              a.get_SourcePositionStart());
        }
      }
    }
  }

}
