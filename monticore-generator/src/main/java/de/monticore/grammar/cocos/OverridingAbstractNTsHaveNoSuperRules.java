/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import java.util.List;
import java.util.Optional;

import de.monticore.codegen.mc2cd.MCGrammarSymbolTableHelper;
import de.monticore.grammar.grammar._ast.ASTAbstractProd;
import de.monticore.grammar.grammar._cocos.GrammarASTAbstractProdCoCo;
import de.monticore.grammar.symboltable.MCGrammarSymbol;
import de.se_rwth.commons.logging.Log;

/**
 * Checks that overriding abstract nonterminals do not have super rules or classes.
 *
 * @author KH
 */
public class OverridingAbstractNTsHaveNoSuperRules implements GrammarASTAbstractProdCoCo {
  
  public static final String ERROR_CODE = "0xA4002";
  
  public static final String ERROR_MSG_FORMAT = " The abstract production %s overriding a production of " +
          "a super grammar must not extend the production %s.\n" +
          "Hint: Overriding productions can only implement interfaces.";
  
  @Override
  public void check(ASTAbstractProd a) {
    Optional<MCGrammarSymbol> grammarSymbol = MCGrammarSymbolTableHelper
        .getMCGrammarSymbol(a);
    List<MCGrammarSymbol> grammarSymbols = grammarSymbol.get().getSuperGrammarSymbols();
    
    if (!a.getSuperRuleList().isEmpty() || !a.getASTSuperClassList().isEmpty()) {
      String extendedType;
      if (!a.getSuperRuleList().isEmpty()){
        extendedType = a.getSuperRuleList().get(0).getName();
      }
      else{
        extendedType = a.getASTSuperClassList().get(0).getTypeName();
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
