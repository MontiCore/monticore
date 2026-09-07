/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.javagen;

import de.monticore.codegen.CodeGenOperationPrinter;
import de.monticore.codegen.javagen.operationprinter.JavaAssignmentOperationHandler;
import de.monticore.codegen.javagen.operationprinter.JavaEqualityOperationHandler;
import de.monticore.codegen.javagen.operationprinter.JavaNumericOperationHandler;
import de.monticore.codegen.javagen.operationprinter.JavaStringConcatenationOperationHandler;
import de.se_rwth.commons.logging.Log;

import java.util.List;

public class JavaOperationPrinter extends CodeGenOperationPrinter {

  protected JavaOperationPrinter() {
    setOperatorHandlers(List.of(
        new JavaEqualityOperationHandler(),
        new JavaAssignmentOperationHandler(),
        new JavaNumericOperationHandler(),
        new JavaStringConcatenationOperationHandler()
    ));
  }

  // static delegate
  public static void init() {
    Log.trace("init JavaOperationPrinter", "CodeGen setup");
    JavaOperationPrinter converter = new JavaOperationPrinter();
    CodeGenOperationPrinter.setDelegate(converter);
  }

}
