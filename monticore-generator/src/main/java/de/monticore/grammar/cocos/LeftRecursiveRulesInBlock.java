/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.DirectLeftRecursionDetector;
import de.monticore.grammar.grammar._ast.ASTAlt;
import de.monticore.grammar.grammar._ast.ASTBlock;
import de.monticore.grammar.grammar._ast.ASTClassProd;
import de.monticore.grammar.grammar._cocos.GrammarASTClassProdCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Checks that blocks do not contain left recursive rules
 * If Antlr (Antlr 4.5 throws an exception) can take care of it,  the check is 
 * no longer necessary.
 *
 * @author MB
 */
public class LeftRecursiveRulesInBlock implements GrammarASTClassProdCoCo {
  
  public static final String ERROR_CODE = "0xA4056";
  
  public static final String ERROR_MSG_FORMAT = " The left recursive rule %s is not allowed in blocks, because it doesn't work in Antlr. ";
  
  @Override
  public void check(ASTClassProd a) {
    DirectLeftRecursionDetector detector = new DirectLeftRecursionDetector();
    String ruleName = a.getName();
    for (ASTAlt alt : a.getAltList()) {
      if (!alt.getComponentList().isEmpty() && alt.getComponentList().get(0) instanceof ASTBlock) {
        if (detector.isAlternativeLeftRecursive(alt, ruleName)) {
          Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT, ruleName),
              a.get_SourcePositionStart());
          return;
        }
      }
    }
  }
  
}
