// (c) https://github.com/MontiCore/monticore
package de.monticore.codegen.javagen.typeconverter;

import de.monticore.codegen.CodeGenPrintAction;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeOfFunction;
import de.monticore.types.check.SymTypeOfGenerics;
import de.se_rwth.commons.logging.Log;

import static de.monticore.codegen.CodeGenSymTypeExpressionConverter.printConverted;
import static de.monticore.codegen.javagen.SymTypeExpression2JavaConverter.getAsJavaType;
import static de.monticore.codegen.javagen.SymTypeExpression2JavaConverter.printJavaType;
import static de.monticore.types3.SymTypeRelations.box;

/**
 * Conversions between function types
 */
public class JavaFunctionConversionHandler
    extends AbstractJavaTypeConverter {

  @Override
  public boolean tryPrintConverted(
      IndentPrinter printer,
      SymTypeExpression modelTargetType,
      SymTypeExpression modelSourceType,
      CodeGenPrintAction sourceExprPrintAction
  ) {
    if (modelTargetType.isFunctionType() && modelSourceType.isFunctionType()) {
      // example target: Student -> Vehicle
      SymTypeOfFunction targetFunc = modelTargetType.asFunctionType();
      // example source: Person -> Car
      SymTypeOfFunction sourceFunc = modelSourceType.asFunctionType();
      int numArgs = targetFunc.sizeArgumentTypes();
      if (targetFunc.isElliptic() || sourceFunc.isElliptic()) {
        // to be extended
        Log.error("0xFD235 No elliptic function support yet");
        return false;
      }

      // cast to target function type
      // (Function1<Vehicle, Student>)
      SymTypeOfGenerics javaTargetFuncType = getAsJavaType(targetFunc).asGenericType();
      printJavaCasted(printer, javaTargetFuncType, p -> {
        // lambda parameters and arrow
        // (Student arg0) ->
        p.print("(");
        for (int i = 0; i < numArgs; i++) {
          if (i > 0) {
            p.print(", ");
          }
          SymTypeExpression paramType = targetFunc.getArgumentType(i);
          SymTypeExpression javaParamType = javaTargetFuncType.getArgument(i + 1); //getJavaType(paramType);
          String javaParamTypeStr = printJavaType(javaParamType);
          p.print(javaParamTypeStr);
          p.print(" ");
          p.print("arg" + i);
        }
        p.print(")");
        p.print(" -> ");

        // cast return value of function
        // (Vehicle)
        printConverted(p, box(targetFunc.getType()), sourceFunc.getType(), p2 -> {
          // using source function
          // getCar.apply
          sourceExprPrintAction.print(p2);
          p2.print(".apply");

          // provide arguments
          // ((Person) arg0)
          p2.print("(");
          for (int i = 0; i < numArgs; i++) {
            String argName = "arg" + i;
            if (i > 0) {
              p2.print(", ");
            }
            // cast arguments
            // (Person) arg0
            SymTypeExpression sourceParam = sourceFunc.getArgumentType(i);
            SymTypeExpression targetParam = targetFunc.getArgumentType(i);
            printConverted(p2, sourceParam, targetParam, p3 -> {
              p3.print(argName);
            });
          }
          p2.print(")");
        });
      });
      return true;
    }
    return false;
  }

}
