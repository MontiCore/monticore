/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.javagen;

import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeOfFunction;
import de.monticore.types.check.SymTypeOfGenerics;
import de.monticore.types.check.SymTypeOfIntersection;
import de.monticore.types.check.SymTypeOfNull;
import de.monticore.types.check.SymTypeOfNumericWithSIUnit;
import de.monticore.types.check.SymTypeOfRegEx;
import de.monticore.types.check.SymTypeOfSIUnit;
import de.monticore.types.check.SymTypeOfTuple;
import de.monticore.types.check.SymTypeOfUnion;
import de.monticore.types.check.SymTypeOfWildcard;
import de.monticore.types3.util.SymTypePrintFullNameVisitor;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static de.monticore.codegen.javagen.SymTypeExpression2JavaConverter.getBoxedJavaTypePrint;

/**
 * Prints SymTypeExpressions in a Java compatible way.
 */
public class SymTypeExpressionJavaPrinterVisitor
    extends SymTypePrintFullNameVisitor {

  protected final static String RTE_PACKAGE = "de.monticore.rte";

  protected final static String JAVA_OBJECT = "java.lang.Object";

  protected final static String JAVA_STRING = "java.lang.String";

  protected final Map<String, String> javaTypeSymbolMap;

  public SymTypeExpressionJavaPrinterVisitor() {
    javaTypeSymbolMap = Map.ofEntries(
        Map.entry("Stream.Stream", RTE_PACKAGE + ".streams.Stream"),
        Map.entry("EventStream.EventStream", RTE_PACKAGE + ".streams.EventStream"),
        Map.entry("SyncStream.SyncStream", RTE_PACKAGE + ".streams.SyncStream"),
        Map.entry("ToptStream.ToptStream", RTE_PACKAGE + ".streams.ToptStream"),
        Map.entry("UntimedStream.UntimedStream", RTE_PACKAGE + ".streams.UntimedStream")
    );
  }

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
    }
    else {
      getPrint().append(RTE_PACKAGE).append(".actions.Action");
    }
    getPrint().append(func.sizeArgumentTypes());

    List<SymTypeExpression> resArgs = new ArrayList<>();
    if (isFunc) {
      resArgs.add(func.getType());
    }
    resArgs.addAll(func.getArgumentTypeList());
    printTypeArguments(resArgs);
  }

  @Override
  public void visit(SymTypeOfGenerics generic) {
    getPrint().append(printTypeSymbol(generic.getTypeInfo()));
    printTypeArguments(generic.getArgumentList());
  }

  @Override
  public void visit(SymTypeOfIntersection intersection) {
    getPrint().append(JAVA_OBJECT);
  }

  @Override
  public void visit(SymTypeOfNull nullType) {
    Log.error("0xFD322 internal error: "
        + "Tried to convert SymTypeOfNull to Java");
  }

  @Override
  public void visit(SymTypeOfRegEx regEx) {
    getPrint().append(JAVA_STRING);
  }

  @Override
  public void visit(SymTypeOfTuple tuple) {
    String className = RTE_PACKAGE + ".tuples.Tuple"
        + tuple.sizeTypes();
    getPrint().append(className);
    printTypeArguments(tuple.getTypeList());
  }

  @Override
  public void visit(SymTypeOfUnion union) {
    getPrint().append(JAVA_OBJECT);
  }

  @Override
  public void visit(SymTypeOfSIUnit siUnit) {
    getPrint().append("double");
  }

  @Override
  public void visit(SymTypeOfNumericWithSIUnit numericWithSIUnit) {
    numericWithSIUnit.getNumericType().accept(this);
  }

  @Override
  public void visit(SymTypeOfWildcard wildcard) {
    getPrint().append("?");
    if (wildcard.hasBound()) {
      if (wildcard.isUpper()) {
        getPrint().append(" extends ");
      }
      else {
        getPrint().append(" super ");
      }
      getPrint().append(getBoxedJavaTypePrint(wildcard.getBound()));
    }
  }

  // hook-points

  @Override
  protected String printTypeSymbol(TypeSymbol symbol) {
    if (javaTypeSymbolMap.containsKey(symbol.getFullName())) {
      return javaTypeSymbolMap.get(symbol.getFullName());
    }
    return symbol.getFullName();
  }

  @Override
  protected String printTypeVarSymbol(TypeVarSymbol symbol) {
    // Java does not support fully qualified type variables
    return symbol.getName();
  }

  @Override
  protected void printOpeningBracketForInner(SymTypeExpression symType) {
    // no-op, as Java does not support bracket-types
  }

  @Override
  protected void printClosingBracketForInner(SymTypeExpression symType) {
    // no-op, as Java does not support bracket-types
  }

  protected void printTypeArguments(List<SymTypeExpression> typeArgs) {
    if (typeArgs.isEmpty()) {
      return;
    }
    getPrint().append('<');
    for (int i = 0; i < typeArgs.size(); i++) {
      SymTypeExpression innerType = typeArgs.get(i);
      printOpeningBracketForInner(innerType);
      getPrint().append(getBoxedJavaTypePrint(innerType));
      printClosingBracketForInner(innerType);
      if (i < typeArgs.size() - 1) {
        getPrint().append(", ");
      }
    }
    getPrint().append('>');
  }

}
