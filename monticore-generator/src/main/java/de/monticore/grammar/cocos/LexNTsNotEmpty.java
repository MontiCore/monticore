/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar._ast.ASTConstantsGrammar;
import de.monticore.grammar.grammar._ast.ASTLexAlt;
import de.monticore.grammar.grammar._ast.ASTLexBlock;
import de.monticore.grammar.grammar._ast.ASTLexChar;
import de.monticore.grammar.grammar._ast.ASTLexCharRange;
import de.monticore.grammar.grammar._ast.ASTLexComponent;
import de.monticore.grammar.grammar._ast.ASTLexNonTerminal;
import de.monticore.grammar.grammar._ast.ASTLexProd;
import de.monticore.grammar.grammar._ast.ASTLexSimpleIteration;
import de.monticore.grammar.grammar._ast.ASTLexString;
import de.monticore.grammar.grammar._cocos.GrammarASTLexProdCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Checks that used nonterminals are lexical nonterminals.
 *
 * @author KH
 */
public class LexNTsNotEmpty implements GrammarASTLexProdCoCo {
  
  public static final String ERROR_CODE = "0xA4015";
  
  public static final String ERROR_MSG_FORMAT = " The lexical production %s must not allow the empty token.";
  
  @Override
  public void check(ASTLexProd a) {
    for (ASTLexAlt alt : a.getAltList()) {
      if (alt.getLexComponentList().isEmpty()) {
        Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT, a.getName()),
            a.get_SourcePositionStart());
        return;
      }
      else {
        for (ASTLexComponent rc : alt.getLexComponentList()) {
          if (rc instanceof ASTLexBlock) {
            if (((ASTLexBlock) rc).getIteration() == ASTConstantsGrammar.PLUS
                || ((ASTLexBlock) rc).getIteration() == ASTConstantsGrammar.DEFAULT) {
              return;
            }
          }
          else if (rc instanceof ASTLexSimpleIteration) {
            if (((ASTLexSimpleIteration) rc).getIteration() == ASTConstantsGrammar.PLUS
                || ((ASTLexSimpleIteration) rc).getIteration() == ASTConstantsGrammar.DEFAULT) {
              return;
            }
          }
          else if (rc instanceof ASTLexNonTerminal
              || rc instanceof ASTLexString
              || rc instanceof ASTLexChar
              || rc instanceof ASTLexCharRange
              || rc instanceof ASTLexString) {
            return;
          }
        }
      }
    }
    Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT, a.getName()),
        a.get_SourcePositionStart());
  }
}
