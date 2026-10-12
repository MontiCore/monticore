/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.javagen.typeconverter;

import de.monticore.codegen.CodeGenPrintAction;
import de.monticore.codegen.ICodeGenSymTypeExpressionConversionHandler;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.check.SymTypeExpression;

import static de.monticore.codegen.javagen.SymTypeExpression2JavaConverter.getJavaTypePrint;

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
}
