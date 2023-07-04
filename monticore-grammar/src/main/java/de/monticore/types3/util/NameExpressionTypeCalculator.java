// (c) https://github.com/MontiCore/monticore
package de.monticore.types3.util;

import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsScope;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symboltable.IScope;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.SymTypeOfFunction;
import de.se_rwth.commons.logging.Log;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * contains the code to derive / synthesize the type of a single name.
 * Names may contain a qualifier, e.g.,
 * a.b.c with a.b being a qualifier and a.b.c being a type would resolved,
 * a.b.c with a.b being a type
 * and c being a type within a.b would NOT be resolved.
 */
public class NameExpressionTypeCalculator {

  protected TypeContextCalculator typeCtxCalc;

  protected WithinTypeBasicSymbolsResolver withinTypeResolver;

  protected NameExpressionTypeCalculator(
      TypeContextCalculator typeCtxCalc,
      WithinTypeBasicSymbolsResolver withinTypeResolver
  ) {
    this.typeCtxCalc = typeCtxCalc;
    this.withinTypeResolver = withinTypeResolver;
  }

  public NameExpressionTypeCalculator() {
    // default values
    typeCtxCalc = new TypeContextCalculator();
    withinTypeResolver = new WithinTypeBasicSymbolsResolver();
  }

  public void setWithinTypeBasicSymbolsResolver(
      WithinTypeBasicSymbolsResolver withinTypeResolver) {
    this.withinTypeResolver = withinTypeResolver;
  }

  public void setTypeContextCalculator(TypeContextCalculator typeCtxCalc) {
    this.typeCtxCalc = typeCtxCalc;
  }

  protected TypeContextCalculator getTypeCtxCalc() {
    return typeCtxCalc;
  }

  protected WithinTypeBasicSymbolsResolver getWithinTypeResolver() {
    return withinTypeResolver;
  }

  public Optional<SymTypeExpression> typeOfNameAsExpr(
      IBasicSymbolsScope enclosingScope,
      String name) {
    // collect all (potential) types
    Set<SymTypeExpression> types = new HashSet<>();

    // to circumvent current shortcomings in our resolver,
    // we resolve with the resolver AND resolve with the within type resolver
    // afterwards we evaluate which result to use

    // not necessarily in an enclosing type
    Optional<SymTypeExpression> optVar = enclosingScope.resolveVariable(
            name, AccessModifier.ALL_INCLUSION, getVariablePredicate())
        .map(v -> v.getType());
    List<SymTypeOfFunction> funcs = enclosingScope
        .resolveFunctionMany(name, getFunctionPredicate()).stream()
        // todo remove creation of a set
        // after resolver is fixed to not return duplicates
        .collect(Collectors.toSet())
        .stream()
        .map(FunctionSymbol::getFunctionType)
        .collect(Collectors.toList());
    // within type
    Optional<TypeSymbol> enclosingType =
        getTypeCtxCalc().getEnclosingType(enclosingScope);
    Optional<SymTypeExpression> varInType = Optional.empty();
    List<SymTypeOfFunction> funcsInType = Collections.emptyList();
    if (enclosingType.isPresent()) {
      SymTypeExpression enclosingTypeExpr =
          SymTypeExpressionFactory.createFromSymbol(enclosingType.get());
      AccessModifier modifier = getTypeCtxCalc()
          .getAccessModifier(enclosingType.get(), enclosingScope);
      varInType = getWithinTypeResolver().resolveVariable(
          enclosingTypeExpr, name, modifier, getVariablePredicate());
      funcsInType = getWithinTypeResolver().resolveFunctions(
          enclosingTypeExpr, name, modifier, getFunctionPredicate());
    }
    // get the correct variable
    if (varInType.isPresent() && optVar.isPresent()) {
      if (varInType.get().getTypeInfo() == optVar.get().getTypeInfo()) {
        types.add(varInType.get());
      }
      else if (optVar.get().getTypeInfo().getEnclosingScope()
          .isProperSubScopeOf(varInType.get().getTypeInfo().getEnclosingScope())) {
        types.add(optVar.get());
      }
      else {
        types.add(varInType.get());
      }
    }
    else if (varInType.isPresent()) {
      types.add(varInType.get());
    }
    else if (optVar.isPresent()) {
      types.add(optVar.get());
    }
    // get the correct functions
    // heuristic, as we assume the resolver to be extended in the near future,
    // and the correct solution would take longer to implement,
    // in Javalight, these cases may never happen anyways
    types.addAll(funcsInType);
    for (SymTypeOfFunction func : funcs) {
      if (funcsInType.stream().noneMatch(f -> f.getSymbol() == func.getSymbol())) {
        types.add(func);
      }
    }

    if (types.size() <= 1) {
      return types.stream().findAny();
    }
    else {
      // this can be extended to mark the intersection as
      // an intersection that one has to select a type of.
      // The current interpretation is, that the result is all the possible types,
      // e.g. "a.b" could have the types of (int, int->int, boolean->int).
      // in Java, this would be filtered out earlier,
      // however, we support more (e.g. SymTypeOfFunction).
      return Optional.of(SymTypeExpressionFactory.createIntersection(types));
    }
  }

  /**
   * used to filter function symbols
   * this is an extension point to,
   * e.g., filter out constructors in OO-Symbols
   */
  protected Predicate<FunctionSymbol> getFunctionPredicate() {
    return f -> true;
  }

  /**
   * used to filter variable symbols
   * this is an extension point
   */
  protected Predicate<VariableSymbol> getVariablePredicate() {
    return v -> true;
  }

  public Optional<SymTypeExpression> typeOfNameAsTypeId(
      IBasicSymbolsScope enclosingScope,
      String name) {
    Optional<SymTypeExpression> type;
    // variable
    Optional<TypeVarSymbol> optTypeVar = enclosingScope.resolveTypeVar(
        name, AccessModifier.ALL_INCLUSION, getTypeVarPredicate()
    );
    // object
    Optional<TypeSymbol> optObj = enclosingScope.resolveType(name,
        AccessModifier.ALL_INCLUSION, getTypePredicate()
            .and(t -> optTypeVar.map(tv -> tv != t).orElse(true))
    );
    // in Java the type variable is preferred
    // e.g. class C<U>{class U{} U v;} //new C<Float>().v has type Float
    if (optTypeVar.isPresent() && optObj.isPresent()) {
      Log.info("found type variable and object type for \""
              + name
              + "\", selecting type variable",
          "TypeVisitor");
    }
    if (optTypeVar.isPresent()) {
      type = Optional.of(SymTypeExpressionFactory
          .createTypeVariable(optTypeVar.get()));
    }
    else if (optObj.isPresent()) {
      type = Optional.of(SymTypeExpressionFactory
          .createFromSymbol(optObj.get())
      );
    }
    else {
      type = Optional.empty();
    }
    return type;
  }

  /**
   * used to filter type symbols
   * this is NOT used to filter type variables
   * this is an extension point
   */
  protected Predicate<TypeSymbol> getTypePredicate() {
    return t -> true;
  }

  /**
   * used to filter type variable symbols
   * this is an extension point
   */
  protected Predicate<TypeVarSymbol> getTypeVarPredicate() {
    return tv -> true;
  }

  // Helper

  protected IBasicSymbolsScope getAsBasicSymbolsScope(IScope scope) {
    // is accepted only here, decided on 07.04.2020
    if (!(scope instanceof IBasicSymbolsScope)) {
      Log.error("0xA2307 the enclosing scope of the expression"
          + "does not implement the interface IBasicSymbolsScope");
    }
    // is accepted only here, decided on 07.04.2020
    return (IBasicSymbolsScope) scope;
  }

}
