/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.javagen.typeconverter;

import de.monticore.codegen.CodeGenPrintAction;
import de.monticore.codegen.ICodeGenSymTypeExpressionConversionHandler;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.check.SymTypeExpression;

import java.util.function.Predicate;

import static de.monticore.codegen.javagen.SymTypeExpression2JavaConverter.getJavaTypePrint;
import static de.monticore.types3.SymTypeRelations.normalize;

public abstract class AbstractJavaTypeConverter
    implements ICodeGenSymTypeExpressionConversionHandler {

  protected void printJavaCasted(
      IndentPrinter printer,
      SymTypeExpression targetType,
      CodeGenPrintAction exprPrintAction) {
    printer.print("((");
    printer.print(getJavaTypePrint(targetType));
    printer.print(") (");
    exprPrintAction.print(printer);
    printer.print("))");
  }

  /**
   * keeps the inner types, e.g., SIUnit prefixes, if already of the kind
   */
  protected SymTypeExpression normalizeUnless(
      SymTypeExpression type,
      Predicate<SymTypeExpression> isOfRequiredKind
  ) {
    return isOfRequiredKind.test(type) ? type : normalize(type);
  }

}
