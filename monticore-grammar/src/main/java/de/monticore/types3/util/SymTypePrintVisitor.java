// (c) https://github.com/MontiCore/monticore
package de.monticore.types3.util;

import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.types.check.*;
import de.monticore.types3.ISymTypeVisitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * prints SymTypeExpressions
 * s. {@link SymTypeExpression#print()}
 * <p>
 * Some of the print methods are dependent on each other
 * with regard to the printing of parentheses.
 * <p>
 * * Usage:
 * calculate(mySymType)
 */
public class SymTypePrintVisitor implements ISymTypeVisitor {

  protected static final String TOP_PRINT = "#TOP";

  protected static final String BOTTOM_PRINT = "#BOTTOM";

  protected static final String OBSCURE_PRINT = "Obscure";

  protected StringBuilder print;

  protected StringBuilder getPrint() {
    return this.print;
  }

  public void reset() {
    this.print = new StringBuilder();
  }

  @Override
  public void visit(SymTypeArray symType) {
    printOpeningBracketForInner(symType.getArgument());
    symType.getArgument().accept(this);
    printClosingBracketForInner(symType.getArgument());
    for (int i = 1; i <= symType.getDim(); i++) {
      getPrint().append("[]");
    }
  }

  @Override
  public void visit(SymTypeObscure symType) {
    getPrint().append(OBSCURE_PRINT);
  }

  @Override
  public void visit(SymTypeOfFunction symType) {
    // print argument types
    // special case to reduce the number of parentheses:
    // int...->int seems less readable than (int...)->int
    if (symType.sizeArgumentTypes() == 1 && !symType.isElliptic()) {
      boolean omitParenthesesAroundArguments =
          // we cannot recognize a function type correctly without parentheses
          !symType.getArgumentType(0).isFunctionType() &&
              // we cannot recognize a tuple type as such without parentheses
              !symType.getArgumentType(0).isTupleType();
      if (!omitParenthesesAroundArguments) {
        getPrint().append("(");
      }
      symType.getArgumentType(0).accept(this);
      if (!omitParenthesesAroundArguments) {
        getPrint().append(')');
      }
    }
    // standard case, guaranteed parentheses around arguments:
    else {
      getPrint().append("(");
      for (int i = 0; i < symType.sizeArgumentTypes(); i++) {
        SymTypeExpression innerType = symType.getArgumentType(i);
        printOpeningBracketForInner(innerType);
        innerType.accept(this);
        printClosingBracketForInner(innerType);
        if (i < symType.sizeArgumentTypes() - 1) {
          getPrint().append(", ");
        }
        else if (symType.isElliptic()) {
          getPrint().append("...");
        }
      }
      getPrint().append(")");
    }
    getPrint().append(" -> ");
    // MCFunction Type is right-associative
    // and unions/intersection have a higher precedence,
    // thus, no parentheses are required
    symType.getType().accept(this);
  }

  @Override
  public void visit(SymTypeOfGenerics symType) {
    getPrint().append(printTypeSymbol(symType.getTypeInfo()));
    getPrint().append('<');
    for (int i = 0; i < symType.sizeArguments(); i++) {
      SymTypeExpression innerType = symType.getArgument(i);
      printOpeningBracketForInner(innerType);
      innerType.accept(this);
      printClosingBracketForInner(innerType);
      if (i < symType.sizeArguments() - 1) {
        getPrint().append(',');
      }
    }
    getPrint().append('>');
  }

  @Override
  public void visit(SymTypeOfIntersection symType) {
    if (symType.isEmptyIntersectedTypes()) {
      getPrint().append(TOP_PRINT);
    }
    else {
      Collection<String> printedTypes = new ArrayList<>();
      for (SymTypeExpression type : symType.getIntersectedTypeSet()) {
        String typePrinted = calculate(type);
        // As function's '->' has a lower precedence than '&'
        // and unions, and intersections are missing them for better readability
        // parentheses are added
        if (isTypePrintedAsInfix(type)) {
          typePrinted = "(" + typePrinted + ")";
        }
        printedTypes.add(typePrinted);
      }
      getPrint().append(printedTypes.stream()
          // sorted to be deterministic
          .sorted()
          .collect(Collectors.joining(" & "))
      );
    }
  }

  @Override
  public void visit(SymTypeOfNull symType) {
    getPrint().append(BasicSymbolsMill.NULL);
  }

  @Override
  public void visit(SymTypeOfObject symType) {
    getPrint().append(printTypeSymbol(symType.getTypeInfo()));
  }

  @Override
  public void visit(SymTypeOfRegEx symType) {
    getPrint()
        .append("R\"")
        .append(symType.getRegExString())
        .append("\"");
  }

  @Override
  public void visit(SymTypeOfTuple symType) {
    getPrint().append("(");
    for (int i = 0; i < symType.sizeTypes(); i++) {
      SymTypeExpression innerType = symType.getType(i);
      printOpeningBracketForInner(innerType);
      innerType.accept(this);
      printClosingBracketForInner(innerType);
      if (i < symType.sizeTypes() - 1) {
        getPrint().append(", ");
      }
    }
    getPrint().append(")");
  }

  @Override
  public void visit(SymTypeOfUnion symType) {
    if (symType.isEmptyUnionizedTypes()) {
      getPrint().append(BOTTOM_PRINT);
    }
    else {
      Collection<String> printedTypes = new ArrayList<>();
      for (SymTypeExpression type : symType.getUnionizedTypeSet()) {
        String typePrinted = calculate(type);
        // As function's '->' has a lower precedence than '&'
        // and unions, and intersections are missing them for better readability
        // parentheses are added
        if (isTypePrintedAsInfix(type)) {
          typePrinted = "(" + typePrinted + ")";
        }
        printedTypes.add(typePrinted);
      }
      getPrint().append(printedTypes.stream()
          // sorted to be deterministic
          .sorted()
          .collect(Collectors.joining(" | "))
      );
    }
  }

  @Override
  public void visit(SymTypePrimitive symType) {
    getPrint().append(symType.getPrimitiveName());
  }

  @Override
  public void visit(SymTypeVariable symType) {
    if (symType.hasTypeVarSymbol()) {
      getPrint().append(printTypeVarSymbol(symType.getTypeVarSymbol()));
    }
    else {
      getPrint().append(symType.getFreeVarIdentifier());
    }
  }

  @Override
  public void visit(SymTypeVoid symType) {
    getPrint().append(BasicSymbolsMill.VOID);
  }

  @Override
  public void visit(SymTypeOfWildcard symType) {
    getPrint().append("?");
    if (symType.hasBound()) {
      if (symType.isUpper()) {
        getPrint().append(" extends ");
      }
      else {
        getPrint().append(" super ");
      }
      printOpeningBracketForInner(symType.getBound());
      symType.getBound().accept(this);
      printClosingBracketForInner(symType.getBound());
    }
  }

  // HookPoints

  protected String printTypeSymbol(TypeSymbol symbol) {
    return symbol.getName();
  }

  protected String printTypeVarSymbol(TypeVarSymbol symbol) {
    return symbol.getName();
  }

  // Helpers

  /**
   * uses this visitor with the provided symType and returns the result.
   * it is reset during the process.
   */
  public String calculate(SymTypeExpression symType) {
    // save state to allow for recursive calling
    StringBuilder oldPrint = getPrint();
    reset();
    symType.accept(this);
    String result = getPrint().toString();
    // restore stack
    this.print = oldPrint;
    return result;
  }

  /**
   * prints an opening bracket if required for the inner type
   */
  protected void printOpeningBracketForInner(SymTypeExpression symType) {
    if (isTypePrintedAsInfix(symType)) {
      getPrint().append('(');
    }
  }

  /**
   * prints a closing bracket if required for the inner type
   */
  protected void printClosingBracketForInner(SymTypeExpression symType) {
    if (isTypePrintedAsInfix(symType)) {
      getPrint().append(')');
    }
  }

  /**
   * returns whether the type is printed as TypeA 'operator' TypeB
   */
  protected boolean isTypePrintedAsInfix(SymTypeExpression symType) {
    return symType.isFunctionType() ||
        symType.isUnionType() ||
        symType.isIntersectionType();
  }

}
