/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar._ast.ASTAbstractProd;
import de.monticore.grammar.grammar._ast.ASTClassProd;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._ast.ASTProd;
import de.monticore.grammar.grammar._ast.ASTRuleReference;
import de.monticore.grammar.grammar._cocos.GrammarASTMCGrammarCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Checks that abstract nonterminals witheout extending productions only occur in a component grammar.
 *
 * @author KH
 */
public class AbstractNTWithoutExtensionOnlyInComponentGrammar implements GrammarASTMCGrammarCoCo {

  public static final String ERROR_CODE = "0xA0277";

  public static final String ERROR_MSG_FORMAT = " The abstract nonterminal %s must not be used without nonterminals " +
          "extending it in a grammar not marked as a grammar component.";

  @Override
  public void check(ASTMCGrammar a) {
    if (!a.isComponent()) {
      for (ASTProd p : a.getAbstractProdList()) {
        boolean extensionFound = false;
        for (ASTAbstractProd ep : a.getAbstractProdList()) {
          for (ASTRuleReference r : ep.getSuperRuleList()) {
            if (p.getName().equals(r.getName())) {
              extensionFound = true;
              break;
            }
          }
          if (extensionFound) {
            break;
          }
        }
        if (!extensionFound) {
          for (ASTClassProd ep : a.getClassProdList()) {
            for (ASTRuleReference r : ep.getSuperRuleList()) {
              if (p.getName().equals(r.getName())) {
                extensionFound = true;
                break;
              }
            }
            if (extensionFound) {
              break;
            }
          }
        }
        if (!extensionFound) {
          Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT, p.getName()),
                  a.get_SourcePositionStart());
        }
      }
    }
  }

}
