// (c) https://github.com/MontiCore/monticore
package de.monticore.expressions.javaclassexpressions.cocos;

import de.monticore.expressions.javaclassexpressions.JavaClassExpressionsMill;
import de.monticore.expressions.javaclassexpressions._ast.ASTGenericInvocationExpression;
import de.monticore.expressions.javaclassexpressions._ast.ASTGenericInvocationSuffix;
import de.monticore.expressions.javaclassexpressions._cocos.JavaClassExpressionsASTGenericInvocationExpressionCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Post-parse trafo
 * Must run before typechecking
 *
 * The NonTerminal matches a lot more than allowed according to Java or
 * currently have a reasonable semantic interpretation,
 * thus, filter out all options that are not valid Java;
 * expr.<argsT>?super(argsP) // not valid
 * expr.<argsT>?super.<argsT2>?.name(argsP) // not valid
 * expr.<argsT>?this(argsP) // not valid
 * expr.<argsT>?name(argsP) // Valid
 * Note: This includes cases where 'expr' actually refers to a type
 * but has parsed as an expression.
 */
public class GenericInvocationExpressionIsASTValid
    implements JavaClassExpressionsASTGenericInvocationExpressionCoCo {

  @Override
  public void check(ASTGenericInvocationExpression node) {
    ASTGenericInvocationSuffix suffix =
        node.getPrimaryGenericInvocationExpression()
            .getGenericInvocationSuffix();
    if (suffix.isSuper() || suffix.isThis()) {
      Log.error("0xFD141 "
              + "the following is not a valid GenericInvocationExpression: "
              + JavaClassExpressionsMill.prettyPrint(node, false)
              + ", please refer to Java Language Specification 21 "
              + "15.8.3+15.8.4 (this)"
              + "15.11 (FieldAccessExpressions), "
              + "15.12 (MethodInvocationExpressions)",
          node.get_SourcePositionStart(),
          node.get_SourcePositionEnd()
      );
    }
  }

}
