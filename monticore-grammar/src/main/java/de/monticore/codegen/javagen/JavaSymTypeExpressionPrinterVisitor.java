/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.javagen;

import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeInferenceVariable;
import de.monticore.types.check.SymTypeObscure;
import de.monticore.types.check.SymTypeOfFunction;
import de.monticore.types.check.SymTypeOfIntersection;
import de.monticore.types.check.SymTypeOfNull;
import de.monticore.types.check.SymTypeOfRegEx;
import de.monticore.types.check.SymTypeOfTuple;
import de.monticore.types.check.SymTypeOfUnion;
import de.monticore.types3.util.SymTypePrintFullNameVisitor;

/**
 * Prints Java compatible SymTypeExpressions in a Java compatible way,
 * The Java compatible SymTypeExpressions can be created with, e.g.,
 * {@link SymTypeExpression2JavaConverter}.
 */
public class JavaSymTypeExpressionPrinterVisitor
    extends SymTypePrintFullNameVisitor {

  @Override
  public void visit(SymTypeInferenceVariable infVar) {
    logKindIsUnsupported(infVar);
  }

  @Override
  public void visit(SymTypeObscure obscure) {
    logKindIsUnsupported(obscure);
  }

  @Override
  public void visit(SymTypeOfFunction func) {
    logKindIsUnsupported(func);
  }

  @Override
  public void visit(SymTypeOfIntersection intersection) {
    logKindIsUnsupported(intersection);
  }

  @Override
  public void visit(SymTypeOfNull nullType) {
    logKindIsUnsupported(nullType);
  }

  @Override
  public void visit(SymTypeOfRegEx regEx) {
    logKindIsUnsupported(regEx);
  }

  @Override
  public void visit(SymTypeOfTuple tuple) {
    logKindIsUnsupported(tuple);
  }

  @Override
  public void visit(SymTypeOfUnion union) {
    logKindIsUnsupported(union);
  }

  // hook-points

  @Override
  protected String printTypeVarSymbol(TypeVarSymbol symbol) {
    // Java does not support fully qualified type variables
    return symbol.getName();
  }

  protected void printOpeningBracketForInner(SymTypeExpression symType) {
    // no-op, as Java does not support bracket-types
  }

  protected void printClosingBracketForInner(SymTypeExpression symType) {
    // no-op, as Java does not support bracket-types
  }

  // helper
  protected void logKindIsUnsupported(SymTypeExpression type) {
    String error = "0xFDCA4 internal error: tried to print type "
        + type.printFullName() + " to Java, even it is an unsupported kind."
        + System.lineSeparator() + "Has SymTypeExpression2JavaConverter been used?";
    throw new UnsupportedOperationException(error);
  }
}
