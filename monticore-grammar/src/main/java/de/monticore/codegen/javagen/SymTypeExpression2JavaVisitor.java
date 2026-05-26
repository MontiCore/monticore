/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.javagen;

import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbolSurrogate;
import de.monticore.types.check.*;
import de.monticore.types3.SymTypeRelations;
import de.monticore.types3.util.SymTypeDeepCloneVisitor;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static de.monticore.types.check.SymTypeExpressionFactory.createGenerics;
import static de.monticore.types.check.SymTypeExpressionFactory.createObscureType;
import static de.monticore.types.check.SymTypeExpressionFactory.createTypeObject;
import static de.monticore.types.check.SymTypeExpressionFactory.createWildcard;
import static de.monticore.types3.SymTypeRelations.box;

public class SymTypeExpression2JavaVisitor extends SymTypeDeepCloneVisitor {

  protected final String RTE_PACKAGE = "de.monticore.rte";

  @Override
  public void visit(SymTypeInferenceVariable infVar) {
    Log.error("0xFD323 internal error: "
        + "Tried to convert a SymTypeInferenceVariable ("
        + infVar.printFullName() + ") to Java.");
    pushTransformedSymType(createObscureType());
  }

  @Override
  public void visit(SymTypeObscure obscure) {
    Log.error("0xFD321 internal error: "
        + "Tried to convert invalid type to Java."
        + "Has a CoCo check been forgotten?");
    pushTransformedSymType(createObscureType());
  }

  @Override
  public void visit(SymTypeOfFunction func) {
    // precondition; support could be extended if required
    if (func.isElliptic()) {
      Log.error("0xFD324 internal error:" +
          "No support for elliptic Functions exists yet.");
      pushTransformedSymType(createObscureType());
      return;
    }

    // Main Symbol: Function or Action?
    String className;
    boolean isFunc = !func.getType().isVoidType();
    if (isFunc) {
      className = RTE_PACKAGE + ".functions.Function";
    }
    else {
      className = RTE_PACKAGE + ".actions.Action";
    }
    className += func.sizeArgumentTypes();
    TypeSymbol funcSym = getRTETypeSymbol(className);

    List<SymTypeExpression> resArgs = new ArrayList<>();
    if (isFunc) {
      resArgs.add(calculate(SymTypeRelations.box(SymTypeRelations.normalize(func.getType()))));
    }
    List<SymTypeExpression> boxedArgs = func.streamArgumentTypes()
        .map(SymTypeRelations::box)
        .map(SymTypeRelations::normalize)
        .collect(Collectors.toList());
    resArgs.addAll(applyToCollection(boxedArgs));

    SymTypeExpression res;
    if (resArgs.isEmpty()) {
      // Action0
      res = createTypeObject(funcSym);
    }
    else {
      res = createGenerics(funcSym, resArgs);
    }
    pushTransformedSymType(res);
  }

  @Override
  public void visit(SymTypeOfGenerics generic) {
    TypeSymbol typeSym = generic.getTypeInfo();
    List<SymTypeExpression> boxedArgs = generic.streamArguments()
        .map(SymTypeRelations::box)
        .collect(Collectors.toList());
    SymTypeOfGenerics res = createGenerics(typeSym, boxedArgs);
    pushTransformedSymType(res);
  }

  @Override
  public void visit(SymTypeOfIntersection intersection) {
    TypeSymbol stringSym = getRTETypeSymbol("java.lang.Object");
    SymTypeOfObject res = createTypeObject(stringSym);
    pushTransformedSymType(res);
  }

  @Override
  public void visit(SymTypeOfNull nullType) {
    Log.error("0xFD322 internal error: "
        + "Tried to convert SymTypeOfNull to Java");
    pushTransformedSymType(createObscureType());
  }

  @Override
  public void visit(SymTypeOfRegEx regEx) {
    TypeSymbol stringSym = getRTETypeSymbol("java.lang.String");
    SymTypeOfObject res = createTypeObject(stringSym);
    pushTransformedSymType(res);
  }

  @Override
  public void visit(SymTypeOfTuple tuple) {
    String className = RTE_PACKAGE + ".tuples.Tuple"
        + tuple.sizeTypes();
    TypeSymbol typeSym = getRTETypeSymbol(className);
    List<SymTypeExpression> boxedArgs = tuple.streamTypes()
        .map(SymTypeRelations::box)
        .map(this::calculate)
        .collect(Collectors.toList());
    SymTypeOfGenerics res = createGenerics(typeSym, boxedArgs);
    pushTransformedSymType(res);
  }

  @Override
  public void visit(SymTypeOfUnion union) {
    TypeSymbol stringSym = getRTETypeSymbol("java.lang.Object");
    SymTypeOfObject res = createTypeObject(stringSym);
    pushTransformedSymType(res);
  }

  @Override
  public void visit(SymTypeOfSIUnit siUnit) {
    // to be extended
    Log.error("0xFD240 conversion of SIUnit to Java is not supported yet.");
  }

  @Override
  public void visit(SymTypeOfNumericWithSIUnit numericWithSIUnit) {
    // to be extended
    Log.error("0xFD241 conversion of SIUnit to Java is not supported yet.");
  }

  @Override
  public void visit(SymTypeVariable typeVar) {
    SymTypeVariable res = typeVar.deepClone();
    pushTransformedSymType(res);
  }

  @Override
  public void visit(SymTypeVoid voidType) {
    Log.error("0xFD325 internal error: "
        + "Tried to convert void to Java.");
    pushTransformedSymType(createObscureType());
  }

  @Override
  public void visit(SymTypeOfWildcard wildcard) {
    SymTypeOfWildcard res;
    if (wildcard.hasBound()) {
      SymTypeExpression resBound = calculate(box(wildcard.getBound()));
      res = createWildcard(wildcard.isUpper(), resBound);
    }
    else {
      res = createWildcard();
    }
    pushTransformedSymType(res);
  }

  // helper

  protected TypeSymbol getRTETypeSymbol(String name) {
    // Not the correct way to create this, only temporary,
    // need SymbolTable of RTE and then resolve
    TypeSymbol typeSym = new TypeSymbolSurrogate(name);
    typeSym.setEnclosingScope(BasicSymbolsMill.globalScope());
    return typeSym;
  }
}
