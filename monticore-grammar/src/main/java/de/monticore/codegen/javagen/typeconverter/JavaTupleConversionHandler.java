/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.javagen.typeconverter;

import de.monticore.codegen.CodeGenPrintAction;
import de.monticore.codegen.util.Node2Name;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeOfTuple;

import static de.monticore.codegen.CodeGenSymTypeExpressionConverter.printConverted;
import static de.monticore.codegen.javagen.SymTypeExpression2JavaConverter.getJavaType;
import static de.monticore.codegen.javagen.SymTypeExpression2JavaConverter.printJavaType;

/**
 * Converts between tuple types
 */
public class JavaTupleConversionHandler extends AbstractJavaTypeConverter {

  protected int tupleNestingLevel = 0;

  @Override
  public boolean tryPrintConverted(
      IndentPrinter printer,
      SymTypeExpression targetType,
      SymTypeExpression sourceType,
      CodeGenPrintAction sourceExprPrintAction
  ) {
    if (sourceType.isTupleType() && targetType.isTupleType()) {
      SymTypeOfTuple sourceTuple = sourceType.asTupleType();
      SymTypeOfTuple targetTuple = targetType.asTupleType();

      String tmpTupleVarName = sourceTuple.getSourceInfo().getSourceNode()
          .map(Node2Name::getName)
          .orElse("_tuple" + tupleNestingLevel);
      tupleNestingLevel++;

      printer.print("((java.util.function.Supplier<");
      printer.print(printJavaType(getJavaType(targetTuple)));
      printer.print(">) () -> { ");
      printer.print(printJavaType(getJavaType(sourceTuple)));
      printer.print(" ");
      printer.print(tmpTupleVarName);
      printer.print(" = ");
      sourceExprPrintAction.print(printer);
      printer.print("; return ");
      printer.print(getJavaType(targetTuple).asGenericType().getTypeConstructorFullName());
      printer.print(".of(");

      for (int i = 0; i < targetType.asTupleType().getTypeList().size(); i++) {
        SymTypeExpression sourceArgType = sourceType.asTupleType().getTypeList().get(i);
        SymTypeExpression targetArgType = targetType.asTupleType().getTypeList().get(i);
        int finalI = i;
        printConverted(printer, targetArgType, sourceArgType, p -> {
          p.print(tmpTupleVarName);
          p.print(".get");
          p.print(finalI);
          p.print("()");
        });
        if (i < getJavaType(targetType).asGenericType().getArgumentList().size() - 1) {
          printer.print(", ");
        }
      }
      printer.print("); }).get()");

      tupleNestingLevel--;
      return true;
    }
    return false;
  }

}
