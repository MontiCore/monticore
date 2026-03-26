/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.javagen;

import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeOfFunction;
import de.monticore.types.check.SymTypeOfGenerics;
import de.monticore.types.check.SymTypeOfTuple;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Prints SymTypeExpressions in a Java compatible way,
 */
public class SymTypeExpressionTypeErasedJavaPrinterVisitor
  extends SymTypeExpressionJavaPrinterVisitor {


  @Override
  public void visit(SymTypeOfFunction func) {
    // precondition; support could be extended if required
    if (func.isElliptic()) {
      Log.error("0xFD324 internal error:" +
        "No support for elliptic Functions exists yet.");
      return;
    }

    // Main Symbol: Function or Action?
    boolean isFunc = !func.getType().isVoidType();
    if (isFunc) {
      getPrint().append(RTE_PACKAGE).append(".functions.Function");
    } else {
      getPrint().append(RTE_PACKAGE).append(".actions.Action");
    }
    getPrint().append(func.sizeArgumentTypes());


    List<SymTypeExpression> resArgs = new ArrayList<>();
    if (isFunc) {
      resArgs.add(func.getType());
    }
    resArgs.addAll(func.getArgumentTypeList());

    if (!resArgs.isEmpty()) {
      getPrint().append('<');
      for (int i = 0; i < resArgs.size(); i++) {
        getPrint().append("?");
        if (i < resArgs.size() - 1) {
          getPrint().append(',');
        }
      }
      getPrint().append('>');
    }
  }

  @Override
  public void visit(SymTypeOfGenerics generic) {
    getPrint().append(printTypeSymbol(generic.getTypeInfo()));
    getPrint().append('<');
    for (int i = 0; i < generic.sizeArguments(); i++) {
      getPrint().append("?");
      if (i < generic.sizeArguments() - 1) {
        getPrint().append(',');
      }
    }
    getPrint().append('>');
  }

  @Override
  public void visit(SymTypeOfTuple tuple) {
    String className = RTE_PACKAGE + ".tuples.Tuple"
      + tuple.sizeTypes();
    getPrint().append(className);
    getPrint().append('<');
    for (int i = 0; i < tuple.sizeTypes(); i++) {
      getPrint().append("?");
      if (i < tuple.sizeTypes() - 1) {
        getPrint().append(',');
      }
    }
    getPrint().append('>');
  }
}
