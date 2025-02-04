/* (c) https://github.com/MontiCore/monticore */

package de.monticore.javalight.cocos;

import de.monticore.javalight._ast.ASTMethodDeclaration;
import de.monticore.javalight._cocos.JavaLightASTMethodDeclarationCoCo;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.TypeCheck;
import de.se_rwth.commons.logging.Log;

public class MethodExceptionThrows implements JavaLightASTMethodDeclarationCoCo {

  public static final String ERROR_CODE = "0xA0811";

  public static final String ERROR_MSG_FORMAT = " No exception of type '%s'  can be thrown. An exception must be a subtype of Throwable.";

  @Override
  public void check(ASTMethodDeclaration node) {
    if (node.isPresentThrows()) {
      SymTypeExpression throwable = SymTypeExpressionFactory.createTypeObject("java.lang.Throwable", node.getEnclosingScope());
      for (SymTypeExpression exception : node.getSymbol().getExceptionsList()) {
        if (!TypeCheck.isSubtypeOf(exception, throwable)) {
          Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT, exception.print()),
                  node.get_SourcePositionStart());
        }
      }
    }
  }

}
