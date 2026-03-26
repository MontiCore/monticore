/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.javagen.typeconverter;

import de.monticore.codegen.CodeGenPrintAction;
import de.monticore.codegen.util.Node2Name;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeOfTuple;

import static de.monticore.codegen.CodeGenSymTypeExpressionConverter.printConverted;
import static de.monticore.codegen.javagen.SymTypeExpression2JavaConverter.convert2JavaType;
import static de.monticore.codegen.javagen.SymTypeExpression2JavaConverter.getJavaTypeConstructor;

/**
 * Converts between tuple types
 */
public class JavaTupleConversionHandler extends AbstractJavaTypeConverter {

  protected int tupleNestingLevel = 0;

  @Override
  public boolean tryPrintConverted(
      IndentPrinter printer,
      SymTypeExpression modelTargetType,
      SymTypeExpression modelSourceType,
      CodeGenPrintAction sourceExprPrintAction
  ) {
    if (modelSourceType.isTupleType() && modelTargetType.isTupleType()) {
      SymTypeOfTuple sourceTuple = modelSourceType.asTupleType();
      SymTypeOfTuple targetTuple = modelTargetType.asTupleType();

      String tmpTupleVarName = sourceTuple.getSourceInfo().getSourceNode()
          .map(Node2Name::getName)
          .orElse("_tuple" + tupleNestingLevel);
      tupleNestingLevel++;

      printer.print("((java.util.function.Supplier<");
      printer.print(convert2JavaType(targetTuple));
      printer.print(">) () -> { ");
      printer.print(convert2JavaType(sourceTuple));
      printer.print(" ");
      printer.print(tmpTupleVarName);
      printer.print(" = ");
      sourceExprPrintAction.print(printer);
      printer.print("; return ");
      printer.print(getJavaTypeConstructor(targetTuple));
      printer.print(".of(");

      for (int i = 0; i < modelTargetType.asTupleType().getTypeList().size(); i++) {
        SymTypeExpression sourceArgType = modelSourceType.asTupleType().getTypeList().get(i);
        SymTypeExpression targetArgType = modelTargetType.asTupleType().getTypeList().get(i);
        int finalI = i;
        printConverted(printer, targetArgType, sourceArgType, p -> {
          p.print(tmpTupleVarName);
          p.print(".get");
          p.print(finalI);
          p.print("()");
        });
        if (i < modelTargetType.asTupleType().getTypeList().size() - 1) {
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
