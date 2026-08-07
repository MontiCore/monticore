/* (c) https://github.com/MontiCore/monticore */

package de.monticore.javalight.cocos;

import de.monticore.javalight._ast.ASTMethodDeclaration;
import de.monticore.javalight._cocos.JavaLightASTMethodDeclarationCoCo;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types3.SymTypeRelations;
import de.monticore.types3.util.OOWithinScopeBasicSymbolsResolver;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;

public class MethodExceptionThrows implements JavaLightASTMethodDeclarationCoCo {

  public static final String ERROR_CODE = "0xA0811";

  public static final String ERROR_MSG_FORMAT =
      " No exception of type '%s'  can be thrown. An exception must be a subtype of Throwable.";

  @Override
  public void check(ASTMethodDeclaration node) {
    if (node.isPresentThrows()) {
      Optional<SymTypeExpression> throwable =
          OOWithinScopeBasicSymbolsResolver.resolveType(node.getEnclosingScope(),
              "java.lang.Throwable");
      if (throwable.isPresent()) {
        for (SymTypeExpression exception : node.getSymbol().getExceptionsList()) {
          if (!SymTypeRelations.isSubTypeOf(exception, throwable.get())) {
            Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT, exception.print()),
                node.get_SourcePositionStart());
          }
        }
      }
    }
  }

}
