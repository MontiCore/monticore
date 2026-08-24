// (c) https://github.com/MontiCore/monticore
package de.monticore.types3.util;

import com.google.common.base.Preconditions;
import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsScope;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.symboltable.modifiers.StaticAccessModifier;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.SymTypeOfFunction;
import de.se_rwth.commons.SourcePosition;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

import static com.google.common.base.Preconditions.checkState;
import static de.monticore.types.check.SymTypeExpressionFactory.createObscureType;

/**
 * Handles (non-)qualified names and chains of names, e.g., {@code a.b.c.d}.
 * <p>
 * This is abstracted of actual ASTNodes,
 * to be reused accordingly.
 * <p>
 * It's main usages are for
 * {@link de.monticore.expressions.expressionsbasis._ast.ASTNameExpression}
 * as well as
 * {@link de.monticore.expressions.commonexpressions._ast.ASTQualifiedNameExpression}.
 */
public class TypeCheck3NameHandler {

  protected static TypeCheck3NameHandler delegate;

  // methods

  /**
   * Takes a name chain and the enclosing scope to calculate
   * what each part of the name is.
   * Example: {@code a.b.c}
   * <p>
   * Disclaimer: Currently not used for MCTypes,
   * may or may not need (small) edits,
   * if use is desired there.
   *
   * @param nameParts           The names in the chain, e.g.,
   *                            {@code a}, {@code b}, {@code c}
   * @param separators          The separators in the chain, e.g.,
   *                            {@code .} twice
   * @param enclosingScope      The enclosing scope of the name chain
   * @param sourcePositionStart The start position of the name chain
   * @param sourcePositionEnd   The end position of the name chain
   * @return The resulting types for expressions/mcTypes of the name chain.
   */
  static public TypeCheck3NameHandlerResult handleName(
      List<String> nameParts,
      List<String> separators,
      IBasicSymbolsScope enclosingScope,
      SourcePosition sourcePositionStart,
      SourcePosition sourcePositionEnd
  ) {
    return getDelegate()._handleName(
        nameParts,
        separators,
        enclosingScope,
        sourcePositionStart,
        sourcePositionEnd
    );
  }

  protected TypeCheck3NameHandlerResult _handleName(
      List<String> nameParts,
      List<String> separators,
      IBasicSymbolsScope enclosingScope,
      SourcePosition sourcePositionStart,
      SourcePosition sourcePositionEnd
  ) {
    // setup
    TypeCheck3NameHandlerASTInput astInput = new TypeCheck3NameHandlerASTInput(
        nameParts,
        separators,
        enclosingScope,
        sourcePositionStart,
        sourcePositionEnd
    );
    TypeCheck3NameHandlerResult result =
        new TypeCheck3NameHandlerResult(astInput);

    // handle name chain from left to right
    for (int i = 0; i < nameParts.size(); i++) {
      NameTypingResultExpectation expectedResult = i == nameParts.size() - 1
          ? NameTypingResultExpectation.EXPRESSION_TYPE
          : NameTypingResultExpectation.OPTIONAL;
      handleLeftMostNameChain(
          astInput.getSubList(i + 1),
          expectedResult,
          result
      );
      if (result.hasTypedNamePart()) {
        break;
      }
    }

    // if we found an MCType, search inside it
    int checkAgainstInvalidLoops = 0;
    while (result
        .getExprTypeOfNamePart(result.getIdxOfLastTypedNamePart())
        .isEmpty()
    ) {
      int i = result.getIdxOfLastTypedNamePart() + 1;
      NameTypingResultExpectation expectedResult = i == nameParts.size() - 1
          ? NameTypingResultExpectation.EXPRESSION_TYPE
          : NameTypingResultExpectation.ANY;
      handleMCTypeNamedAccess(
          astInput.getSubList(i + 1),
          expectedResult,
          result
      );
      Preconditions.checkState(checkAgainstInvalidLoops++ < 10000);
    }

    // after finding an expression, we don't expect any MCTypes anymore
    for (int i = result.getIdxOfLastTypedNamePart() + 1; i < nameParts.size(); i++) {
      handleValueNamedAccess(
          astInput.getSubList(i + 1),
          NameTypingResultExpectation.EXPRESSION_TYPE,
          result
      );
    }

    result.assertIsCleanedUpAfterCalculations();
    return result;
  }

  // case: (qualifier ".")? name
  protected void handleLeftMostNameChain(
      TypeCheck3NameHandlerASTInput astInput,
      NameTypingResultExpectation expectedResult,
      TypeCheck3NameHandlerResult result
  ) {
    // case: (qualifier ".")? name as Expression
    Optional<SymTypeExpression> exprType = calculateExprQNameOrLogError(
        astInput,
        expectedResult != NameTypingResultExpectation.EXPRESSION_TYPE
    );
    // case (qualifier ".")? name as MCType
    // this requires an outer field-access (qualifier.name.field),
    // as the end result has to be an expression
    Optional<SymTypeExpression> mcTypeType = Optional.empty();
    if (exprType.isEmpty() &&
        expectedResult == NameTypingResultExpectation.OPTIONAL
    ) {
      mcTypeType = calculateMCTypeQName(astInput);
    }

    result.setExprOfNamePart(astInput.getSize() - 1, exprType);
    result.setMCTypeTypeOfNamePart(astInput.getSize() - 1, mcTypeType);
  }

  /**
   * case: (qName ".")? name,
   * e.g., myPerson, package.artifact.staticVar.
   * will log an error if necessary (resultsAreOptional).
   */
  protected Optional<SymTypeExpression> calculateExprQNameOrLogError(
      TypeCheck3NameHandlerASTInput astInput,
      boolean resultsAreOptional
  ) {
    // case qualifier "." name as an expression
    Optional<SymTypeExpression> type =
        calculateExprQName(astInput, resultsAreOptional);
    if (type.isEmpty() && !resultsAreOptional) {
      Log.error("0xF735F unable to interpret qualified name \""
              + astInput.getNameAsString()
              + "\" as expression",
          astInput.getSourcePositionStart(),
          astInput.getSourcePositionEnd()
      );
      type = Optional.of(createObscureType());
    }
    return type;
  }

  /**
   * calculates (a.b.)c as expression with a.b being an optional qualifier
   */
  protected Optional<SymTypeExpression> calculateExprQName(
      TypeCheck3NameHandlerASTInput astInput,
      boolean resultsAreOptional) {
    String name = astInput.getNameAsString();
    Optional<SymTypeExpression> type = WithinScopeBasicSymbolsResolver
        .resolveNameAsExpr(astInput.getEnclosingScope(), name);
    if (type.isEmpty() && !resultsAreOptional) {
      type = WithinScopeBasicSymbolsResolver
          .resolveTypeAsExpression(astInput.getEnclosingScope(), name);
    }
    return type;
  }

  /**
   * calculates (a.b.)c as mcType with a.b being an optional qualifier.
   * only evaluates qualified names without type arguments
   */
  protected Optional<SymTypeExpression> calculateMCTypeQName(
      TypeCheck3NameHandlerASTInput astInput
  ) {
    return WithinScopeBasicSymbolsResolver.resolveType(
        astInput.getEnclosingScope(),
        astInput.getNameAsString()
    );
  }

  protected void handleMCTypeNamedAccess(
      TypeCheck3NameHandlerASTInput astInput,
      NameTypingResultExpectation expectedResult,
      TypeCheck3NameHandlerResult result
  ) {
    SymTypeExpression innerMCType = result.getMCTypeTypeOfNamePart(
        result.getIdxOfLastTypedNamePart()
    ).get();
    // case: typeIdentifier "." name, e.g., XClass.staticVar
    // in Java, if variable exists, typeIdentifier "." name is ignored,
    // even if variable "." name does not exist
    boolean exprResultsAreOptional =
        expectedResult != NameTypingResultExpectation.EXPRESSION_TYPE;
    Optional<SymTypeExpression> exprType =
        calculateMCTypeStaticAccessOrLogError(
            astInput, innerMCType, exprResultsAreOptional
        );
    // case: typeid "." typeid2 ("." name), e.g., C1.CInner(.staticVar)
    Optional<SymTypeExpression> outerMCType = Optional.empty();
    if (exprType.isEmpty() && exprResultsAreOptional) {
      // always expecting a result here, as we tried expressions already
      outerMCType = calculateInnerMCTypeAccessOrLogError(
          astInput, innerMCType, false
      );
    }

    result.setExprOfNamePart(astInput.getSize() - 1, exprType);
    result.setMCTypeTypeOfNamePart(astInput.getSize() - 1, outerMCType);
  }

  protected Optional<SymTypeExpression> calculateMCTypeStaticAccessOrLogError(
      TypeCheck3NameHandlerASTInput astInput,
      SymTypeExpression innerMCType,
      boolean resultsAreOptional
  ) {
    Optional<SymTypeExpression> type =
        calculateMCTypeStaticAccess(astInput, innerMCType, resultsAreOptional);
    if (type.isEmpty() && !resultsAreOptional) {
      Log.error("0xF736F given MCType of type "
              + innerMCType.printFullName()
              + " unable to derive the type of the access "
              + "\"" + astInput.getSeparators().getLast()
              + astInput.getNameParts().getLast() + "\"",
          astInput.getSourcePositionStart(),
          astInput.getSourcePositionEnd()
      );
      type = Optional.of(createObscureType());
    }
    return type;
  }

  /**
   * calculates a.b.c as expression with a.b being an MCType,
   * e.g., XClass.staticVar
   */
  protected Optional<SymTypeExpression> calculateMCTypeStaticAccess(
      TypeCheck3NameHandlerASTInput astInput,
      SymTypeExpression innerMCType,
      boolean resultsAreOptional
  ) {
    final String name = astInput.getNameParts().getLast();
    Optional<SymTypeExpression> type;
    if (WithinTypeBasicSymbolsResolver.canResolveIn(innerMCType)) {
      AccessModifier modifier = innerMCType.hasTypeInfo() ?
          TypeContextCalculator.getAccessModifier(
              innerMCType.getTypeInfo(),
              astInput.getEnclosingScope(),
              true
          ) : StaticAccessModifier.STATIC;
      type = resolveVariablesAndFunctionsWithinType(
          innerMCType,
          name,
          modifier,
          v -> true,
          f -> true
      );
      if (type.isEmpty() && !resultsAreOptional) {
        type = WithinTypeBasicSymbolsResolver.resolveTypeAsExpression(
            innerMCType,
            name,
            modifier,
            t -> true
        );
      }

      // Log remark about access modifier,
      // if access modifier is the reason it has not been resolved
      if (type.isEmpty() && !resultsAreOptional) {
        Optional<SymTypeExpression> potentialResult =
            resolveVariablesAndFunctionsWithinType(
                innerMCType,
                name,
                AccessModifier.ALL_INCLUSION,
                v -> true,
                f -> true
            );
        if (potentialResult.isPresent()) {
          Log.warn("0xFDE3C tried to resolve \"" + name + "\""
                  + " given MCType "
                  + innerMCType.printFullName()
                  + " and symbols have been found"
                  + ", but due to the access modifiers (e.g., static)"
                  + ", nothing could be resolved.",
              astInput.getSourcePositionStart(),
              astInput.getSourcePositionEnd()
          );
        }
      }
    }
    // extension point
    else {
      Log.error("0xFDE3A unexpected field access \"" + name + "\""
              + " for type " + innerMCType.printFullName(),
          astInput.getSourcePositionStart(),
          astInput.getSourcePositionEnd()
      );
      type = Optional.empty();
    }
    return type;
  }

  protected Optional<SymTypeExpression> calculateInnerMCTypeAccessOrLogError(
      TypeCheck3NameHandlerASTInput astInput,
      SymTypeExpression innerMCType,
      boolean resultsAreOptional
  ) {
    Optional<SymTypeExpression> type =
        calculateInnerMCTypeAccess(astInput, innerMCType, resultsAreOptional);
    if (type.isEmpty() && !resultsAreOptional) {
      Log.error("0xF736E given MCType of type "
              + innerMCType.printFullName()
              + " unable to derive the type of the access "
              + "\"" + astInput.getSeparators().getLast()
              + astInput.getNameParts().getLast() + "\"",
          astInput.getSourcePositionStart(),
          astInput.getSourcePositionEnd()
      );
      type = Optional.of(createObscureType());
    }
    return type;
  }

  /**
   * calculates a.b.c as a type identifier with a.b being a type identifier,
   * e.g., OuterClass.InnerClass(.staticVariable)
   */
  protected Optional<SymTypeExpression> calculateInnerMCTypeAccess(
      TypeCheck3NameHandlerASTInput astInput,
      SymTypeExpression innerMCType,
      boolean resultsAreOptional
  ) {
    final String name = astInput.getNameParts().getLast();
    Optional<SymTypeExpression> type = Optional.empty();
    if (WithinTypeBasicSymbolsResolver.canResolveIn(innerMCType)) {
      AccessModifier modifier = innerMCType.hasTypeInfo() ?
          TypeContextCalculator.getAccessModifier(
              innerMCType.getTypeInfo(),
              astInput.getEnclosingScope(),
              true
          ) : StaticAccessModifier.STATIC;
      type = WithinTypeBasicSymbolsResolver.resolveType(
          innerMCType,
          name,
          modifier,
          t -> true
      );
    }
    else {
      Log.error("0xFDE3A unexpected field access \"" + name + "\""
              + " for type " + innerMCType.printFullName(),
          astInput.getSourcePositionStart(),
          astInput.getSourcePositionEnd()
      );
    }
    return type;
  }

  protected void handleValueNamedAccess(
      TypeCheck3NameHandlerASTInput astInput,
      NameTypingResultExpectation expectedResult,
      TypeCheck3NameHandlerResult result
  ) {
    SymTypeExpression innerExprType = result.getExprTypeOfNamePart(
        result.getIdxOfLastTypedNamePart()
    ).get();
    Optional<SymTypeExpression> exprType = _calculateExprFieldAccessOrLogError(
        astInput, innerExprType, false
    );

    result.setExprOfNamePart(astInput.getSize() - 1, exprType);
    result.setMCTypeTypeOfNamePart(astInput.getSize() - 1, Optional.empty());
  }

  /**
   * calculates a.b with a being an expression.
   * <p>
   * This is specifically public to handle,
   * in addition to chains of names,
   * {@link de.monticore.expressions.commonexpressions._ast.ASTFieldAccessExpression}
   */
  public static Optional<SymTypeExpression> calculateExprFieldAccessOrLogError(
      String name,
      String separator,
      IBasicSymbolsScope enclosingScope,
      SymTypeExpression innerExprType,
      boolean resultsAreOptional,
      SourcePosition startPos,
      SourcePosition endPos
  ) {
    return getDelegate()._calculateExprFieldAccessOrLogError(
        name, separator, enclosingScope, innerExprType, resultsAreOptional,
        startPos, endPos
    );
  }

  /**
   * calculates a.b with a being an expression
   */
  protected Optional<SymTypeExpression> _calculateExprFieldAccessOrLogError(
      TypeCheck3NameHandlerASTInput astInput,
      SymTypeExpression innerExprType,
      boolean resultsAreOptional
  ) {
    return _calculateExprFieldAccessOrLogError(
        astInput.getNameParts().getLast(),
        astInput.getSeparators().getLast(),
        astInput.getEnclosingScope(),
        innerExprType,
        resultsAreOptional,
        astInput.getSourcePositionStart(),
        astInput.getSourcePositionEnd()
    );
  }

  /**
   * case: expression "." name,
   * e.g., myPerson.age.
   * will log an error if necessary (resultsAreOptional).
   */
  protected Optional<SymTypeExpression> _calculateExprFieldAccessOrLogError(
      String name,
      String separator,
      IBasicSymbolsScope enclosingScope,
      SymTypeExpression innerExprType,
      boolean resultsAreOptional,
      SourcePosition startPos,
      SourcePosition endPos
  ) {
    Optional<SymTypeExpression> type = calculateExprFieldAccess(
        name, separator, enclosingScope, innerExprType, resultsAreOptional,
        startPos, endPos
    );
    if (type.isEmpty() && !resultsAreOptional) {
      Log.error("0xF737F given expression of type "
              + innerExprType.printFullName()
              + " unable to derive the type of the access "
              + "\"" + separator + name + "\"."
              + " You may want to check whether"
              + System.lineSeparator()
              + "  1. The element exists in the models/included symboltables"
              + System.lineSeparator()
              + "  2. The element's access modifier is set (e.g., to public)",
          startPos, endPos
      );
      type = Optional.of(createObscureType());
    }
    return type;
  }

  /**
   * calculates a.b with a being an expression
   */
  protected Optional<SymTypeExpression> calculateExprFieldAccess(
      String name,
      String separator,
      IBasicSymbolsScope enclosingScope,
      SymTypeExpression innerExprType,
      boolean resultsAreOptional,
      SourcePosition startPos,
      SourcePosition endPos
  ) {
    Optional<SymTypeExpression> type;
    if (WithinTypeBasicSymbolsResolver.canResolveIn(innerExprType)) {
      AccessModifier modifier = innerExprType.hasTypeInfo() ?
          TypeContextCalculator.getAccessModifier(
              innerExprType.getTypeInfo(),
              enclosingScope
          ) : AccessModifier.ALL_INCLUSION;
      type = resolveVariablesAndFunctionsWithinType(
          innerExprType,
          name,
          modifier,
          v -> true,
          f -> true
      );

      // Log remark about access modifier,
      // if access modifier is the reason it has not been resolved
      if (type.isEmpty() && !resultsAreOptional) {
        Optional<SymTypeExpression> potentialResult =
            resolveVariablesAndFunctionsWithinType(
                innerExprType,
                name,
                AccessModifier.ALL_INCLUSION,
                v -> true,
                f -> true
            );
        if (potentialResult.isPresent()) {
          Log.warn("tried to resolve \"" + separator + name + "\""
                  + " given expression of type "
                  + innerExprType.printFullName()
                  + " and symbols have been found"
                  + ", but due to the access modifiers (e.g., public)"
                  + ", nothing could be resolved",
              startPos, endPos
          );
        }
      }
    }

    // extension point
    else {
      Log.error("0xFDB3A unexpected access "
              + "\"" + separator + name + "\""
              + " for a value of type " + innerExprType.printFullName(),
          startPos, endPos
      );
      type = Optional.empty();
    }
    return type;
  }

  // helper

  /**
   * resolver helper function that searches for functions AND variables
   * in a type at the same time
   */
  protected Optional<SymTypeExpression> resolveVariablesAndFunctionsWithinType(
      SymTypeExpression innerAsExprType,
      String name,
      AccessModifier modifier,
      Predicate<VariableSymbol> varPredicate,
      Predicate<FunctionSymbol> funcPredicate
  ) {
    Set<SymTypeExpression> types = new LinkedHashSet<>();
    Optional<SymTypeExpression> variable =
        WithinTypeBasicSymbolsResolver.resolveVariable(innerAsExprType,
            name,
            modifier,
            varPredicate
        );
    variable.ifPresent(types::add);
    Collection<SymTypeOfFunction> functions =
        WithinTypeBasicSymbolsResolver.resolveFunctions(
            innerAsExprType,
            name,
            modifier,
            funcPredicate
        );
    types.addAll(functions);
    if (types.size() <= 1) {
      return types.stream().findAny();
    }
    else {
      return Optional.of(SymTypeExpressionFactory.createIntersection(types));
    }
  }

  // inner types

  /**
   * Represents the result of handling a name chain.
   * Contains the mapping to expression and MCType types
   * for each part of the name chain.
   */
  static public class TypeCheck3NameHandlerResult {

    protected final List<Optional<SymTypeExpression>> exprTypes;
    protected final List<Optional<SymTypeExpression>> mcTypeTypes;

    public TypeCheck3NameHandlerResult(
        TypeCheck3NameHandlerASTInput astInput
    ) {
      int numNameParts = astInput.getSize();
      Preconditions.checkArgument(numNameParts > 0);
      exprTypes = new ArrayList<>(Collections.nCopies(
          numNameParts, Optional.empty()
      ));
      mcTypeTypes = new ArrayList<>(Collections.nCopies(
          numNameParts, Optional.empty()
      ));
    }

    protected TypeCheck3NameHandlerResult(
        List<Optional<SymTypeExpression>> exprTypes,
        List<Optional<SymTypeExpression>> mcTypeTypes) {
      this.exprTypes = exprTypes;
      this.mcTypeTypes = mcTypeTypes;
    }

    public int size() {
      checkState(exprTypes.size() == mcTypeTypes.size());
      return exprTypes.size();
    }

    public Optional<SymTypeExpression> getExprTypeOfNamePart(int n) {
      return exprTypes.get(n);
    }

    public Optional<SymTypeExpression> getExprTypeOfLastNamePart() {
      return exprTypes.getLast();
    }

    public void setExprTypeOfNamePart(int n, SymTypeExpression type) {
      setExprOfNamePart(n, Optional.of(type));
    }

    public void setExprOfNamePart(int n, Optional<SymTypeExpression> type) {
      this.exprTypes.set(n, type);
    }

    public Optional<SymTypeExpression> getMCTypeTypeOfNamePart(int n) {
      return mcTypeTypes.get(n);
    }

    public Optional<SymTypeExpression> getMCTypeOfLastNamePart() {
      return mcTypeTypes.getLast();
    }

    public void setMCTypeTypeOfNamePart(int n, SymTypeExpression type) {
      setMCTypeTypeOfNamePart(n, Optional.of(type));
    }

    public void setMCTypeTypeOfNamePart(int n, Optional<SymTypeExpression> type) {
      this.mcTypeTypes.set(n, type);
    }

    public int getIdxOfLastTypedNamePart() {
      for (int i = exprTypes.size() - 1; i >= 0; i--) {
        if (exprTypes.get(i).isPresent() || mcTypeTypes.get(i).isPresent()) {
          return i;
        }
      }
      return -1;
    }

    public boolean hasTypedNamePart() {
      return getIdxOfLastTypedNamePart() >= 0;
    }

    public TypeCheck3NameHandlerResult getSublist(int n) {
      return new TypeCheck3NameHandlerResult(
          exprTypes.subList(0, n),
          mcTypeTypes.subList(0, n)
      );
    }

    /**
     * checks, that after all calculations are done,
     * the results are properly cleaned up,
     * no matter what happened during calculations.
     */
    public void assertIsCleanedUpAfterCalculations() {
      checkState(exprTypes.size() == mcTypeTypes.size());

      // check that a type has been found (or an error occurred)
      checkState(exprTypes.getLast().isPresent());
      checkState(mcTypeTypes.getLast().isEmpty());
      if (exprTypes.getLast().get().isObscureType()) {
        // error already logged
        return;
      }

      boolean foundType = false;
      for (int i = 0; i < exprTypes.size(); i++) {
        Optional<SymTypeExpression> exprTypeOpt = exprTypes.get(i);
        Optional<SymTypeExpression> mcTypeOpt = mcTypeTypes.get(i);

        // check that, after a type has been found, only further types follow.
        // aka: no MyClass.someNameSpace.myVariable
        // might need to be changed for some languages in the future.
        if (foundType) {
          checkState(exprTypeOpt.isPresent() || mcTypeOpt.isPresent());
        }
        else if (exprTypeOpt.isPresent() || mcTypeOpt.isPresent()) {
          foundType = true;
        }

        // check that any name is only considered as expression or MCType,
        // but never both
        if (exprTypeOpt.isPresent() || mcTypeOpt.isPresent()) {
          checkState(!(exprTypeOpt.isPresent() && mcTypeOpt.isPresent()));
        }
      }
    }
  }

  /**
   * A simplified input, that allows splitting it up,
   * to make handling names recursive in a clean way.
   */
  static protected class TypeCheck3NameHandlerASTInput {
    protected List<String> nameParts;
    protected List<String> separators;
    protected IBasicSymbolsScope enclosingScope;
    // whole AST
    protected SourcePosition sourcePositionStart;
    protected SourcePosition sourcePositionEnd;

    public TypeCheck3NameHandlerASTInput(
        List<String> nameParts,
        List<String> separators,
        IBasicSymbolsScope enclosingScope,
        SourcePosition sourcePositionStart,
        SourcePosition sourcePositionEnd
    ) {
      this.nameParts = Preconditions.checkNotNull(nameParts);
      this.separators = Preconditions.checkNotNull(separators);
      this.enclosingScope = Preconditions.checkNotNull(enclosingScope);
      this.sourcePositionStart = Preconditions.checkNotNull(sourcePositionStart);
      this.sourcePositionEnd = Preconditions.checkNotNull(sourcePositionEnd);
      Preconditions.checkArgument(!nameParts.isEmpty());
      Preconditions.checkArgument(nameParts.size() == separators.size() + 1);
    }

    public TypeCheck3NameHandlerASTInput getSubList(int n) {
      Preconditions.checkArgument(n > 0);
      Preconditions.checkArgument(n <= getNameParts().size());
      return new TypeCheck3NameHandlerASTInput(
          getNameParts().subList(0, n),
          getSeparators().subList(0, n - 1),
          getEnclosingScope(),
          getSourcePositionStart(),
          getSourcePositionEnd()
      );
    }

    public String getNameAsString() {
      StringBuilder name = new StringBuilder();
      for (int i = 0; i < getSeparators().size(); i++) {
        name.append(getNameParts().get(i));
        name.append(getSeparators().get(i));
      }
      name.append(getNameParts().getLast());
      return name.toString();
    }

    public List<String> getNameParts() {
      return nameParts;
    }

    public int getSize() {
      return nameParts.size();
    }

    public List<String> getSeparators() {
      return separators;
    }

    public IBasicSymbolsScope getEnclosingScope() {
      return enclosingScope;
    }

    public SourcePosition getSourcePositionStart() {
      return sourcePositionStart;
    }

    public SourcePosition getSourcePositionEnd() {
      return sourcePositionEnd;
    }

  }

  /**
   * defines which results are expected of a chain of Names.
   * S.a. {@link #_handleName(List, List, IBasicSymbolsScope, SourcePosition, SourcePosition)}
   */
  protected enum NameTypingResultExpectation {
    /**
     * A type can, but does not have to be calculated,
     * This is the case if the name can refer to a package.
     */
    OPTIONAL,
    /**
     * A type needs to be calculated, however,
     * it can either be of an expression or an MCType.
     * This is the case if a type has already been calculated, e.g.,
     * if a.b is an MCType, a.b.c is required to have a type.
     */
    ANY,
    /**
     * An expression type needs to be calculated.
     * This is the case if this is the rightmost name in the chain, e.g.,
     * a.b.c has to be an expression if there exists no d afterwards: a.b.c.d
     */
    EXPRESSION_TYPE,
  }

  // static delegate

  public static void init() {
    Log.trace("init default TypeCheck3NameHandler", "TypeCheck setup");
    setDelegate(new TypeCheck3NameHandler());
  }

  public static void reset() {
    TypeCheck3NameHandler.delegate = null;
  }

  protected static void setDelegate(
      TypeCheck3NameHandler newDelegate
  ) {
    TypeCheck3NameHandler.delegate = Preconditions.checkNotNull(newDelegate);
  }

  protected static TypeCheck3NameHandler getDelegate() {
    if (TypeCheck3NameHandler.delegate == null) {
      init();
    }
    return TypeCheck3NameHandler.delegate;
  }

}
