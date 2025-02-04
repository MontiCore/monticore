/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._cocos.GrammarASTMCGrammarCoCo;
import de.se_rwth.commons.Joiners;
import de.se_rwth.commons.logging.Log;

/**
 * Checks that the package name is lowercase
 *
 */
public class PackageNameLowerCase implements GrammarASTMCGrammarCoCo {
  
  public static final String ERROR_CODE = "0xA4006";
  
  public static final String ERROR_MSG_FORMAT = " The package name %s contains uppercase letters!";
  
  @Override
  public void check(ASTMCGrammar a) {
    for (String p: a.getPackageList()) {
      for (char c:p.toCharArray()) {
        if (Character.isUpperCase(c)) {
          Log.warn(String.format(ERROR_CODE + ERROR_MSG_FORMAT, Joiners.DOT.join(a.getPackageList())),
                  a.get_SourcePositionStart());
          return;
        }
      }
    }
  }

}
