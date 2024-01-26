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

  protected static final String FREE_VARIABLE_NAME = "__INTERNAL_TYPEVARIABLE";

  protected StringBuilder print;

  protected StringBuilder getPrint() {
    return this.print;
  }

  public void reset() {
    this.print = new StringBuilder();
  }

  @Override
  public void visit(SymTypeArray symType) {
    symType.getArgument().accept(this);
    for (int i = 1; i <= symType.getDim(); i++) {
      getPrint().append("[]");
    }
  }

  @Override
  public void visit(SymTypeObscure symType) {
    getPrint().append("Obscure");
  }

  @Override
  public void visit(SymTypeOfFunction symType) {
    boolean omitParenthesesAroundArguments =
        symType.sizeArgumentTypes() == 1 &&
            // we cannot recognize a function type correctly without parentheses
            !symType.getArgumentType(0).isFunctionType() &&
            // we cannot recognize a tuple type as such without parentheses
            !symType.getArgumentType(0).isTupleType() &&
            // int...->int seems less readable than (int...)->int
            !symType.isElliptic();
    if (!omitParenthesesAroundArguments) {
      getPrint().append("(");
    }
    for (int i = 0; i < symType.sizeArgumentTypes(); i++) {
      symType.getArgumentType(i).accept(this);
      if (i < symType.sizeArgumentTypes() - 1) {
        getPrint().append(", ");
      }
      else if (symType.isElliptic()) {
        getPrint().append("...");
      }
    }
    if (!omitParenthesesAroundArguments) {
      getPrint().append(")");
    }
    getPrint().append(" -> ");
    symType.getType().accept(this);
  }

  @Override
  public void visit(SymTypeOfGenerics symType) {
    getPrint().append(printTypeSymbol(symType.getTypeInfo()));
    getPrint().append('<');
    for (int i = 0; i < symType.sizeArguments(); i++) {
      symType.getArgument(i).accept(this);
      if (i < symType.sizeArguments() - 1) {
        getPrint().append(',');
      }
    }
    getPrint().append('>');
  }

  @Override
  public void visit(SymTypeOfIntersection symType) {
    getPrint().append("(");
    Collection<String> printedTypes = new ArrayList<>();
    for (SymTypeExpression type : symType.getIntersectedTypeSet()) {
      String typePrinted = calculate(type);
      // As function's '->' has a lower precedence than '&',
      // parentheses are added
      if (type.isFunctionType()) {
        typePrinted = "(" + typePrinted + ")";
      }
      printedTypes.add(typePrinted);
    }
    getPrint().append(printedTypes.stream()
        // sorted to be deterministic
        .sorted()
        .collect(Collectors.joining(" & "))
    );
    getPrint().append(")");
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
    getPrint().append(symType.streamTypes()
        .map(SymTypeExpression::print)
        .collect(Collectors.joining(", "))
    );
    getPrint().append(")");
  }

  @Override
  public void visit(SymTypeOfUnion symType) {
    getPrint().append("(");
    Collection<String> printedTypes = new ArrayList<>();
    for (SymTypeExpression type : symType.getUnionizedTypeSet()) {
      String typePrinted = calculate(type);
      // As function's '->' has a lower precedence than '|',
      // parentheses are added
      if (type.isFunctionType()) {
        typePrinted = "(" + typePrinted + ")";
      }
      printedTypes.add(typePrinted);
    }
    getPrint().append(printedTypes.stream()
        // sorted to be deterministic
        .sorted()
        .collect(Collectors.joining(" | "))
    );
    getPrint().append(")");
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
      getPrint().append(FREE_VARIABLE_NAME);
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
      symType.getBound().accept(this);
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

}
