package de.monticore.statements.mccommonstatements.cocos;

import de.monticore.statements.mccommonstatements._ast.ASTConstantsMCCommonStatements;
import de.monticore.statements.mccommonstatements._ast.ASTJavaModifier;
import de.monticore.statements.mccommonstatements._cocos.MCCommonStatementsASTJavaModifierCoCo;
import de.se_rwth.commons.logging.Log;

public class SealedModifierUsage implements MCCommonStatementsASTJavaModifierCoCo {
  
  public static final String ERROR_CODE = "0xA0913";
  
  public static final String ERROR_MESSAGE = "Modifier '%s' must not be used.";
  
  @Override
  public void check(ASTJavaModifier modifier) {
    if (modifier.getModifier() == ASTConstantsMCCommonStatements.SEALED) {
      Log.error(String.format(ERROR_CODE + ERROR_MESSAGE, "sealed"),
          modifier.get_SourcePositionStart());
    }
    else if (modifier.getModifier() == ASTConstantsMCCommonStatements.NON_SEALED) {
      Log.error(String.format(ERROR_CODE + ERROR_MESSAGE, "non-sealed"),
          modifier.get_SourcePositionStart());
    }
  }
}
