// (c) https://github.com/MontiCore/monticore
package de.monticore.types3.generics.util;

import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsScope;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeInferenceVariable;
import de.monticore.types.check.SymTypeVariable;
import de.monticore.types3.generics.TypeParameterRelations;
import de.monticore.types3.util.SymTypeCollectionVisitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Default delegatee for TypeParameterRelations.
 * This itself delegates to the individual implementations.
 */
public class TypeParameterRelationsDefaultDelegatee
    extends TypeParameterRelations {

  // delegates

  protected SymTypeVariableReplaceVisitor typeVarReplacer;

  protected SymTypeInferenceVariableReplaceVisitor infVarReplacer;

  protected SymTypeFreeVariableReplaceVisitor freeVariableReplacer;

  protected SymTypeCollectionVisitor symTypeCollectionVisitor;

  protected WildcardCapturer wildCardCapturer;

  public TypeParameterRelationsDefaultDelegatee() {
    this.typeVarReplacer = new SymTypeVariableReplaceVisitor();
    this.infVarReplacer = new SymTypeInferenceVariableReplaceVisitor();
    this.freeVariableReplacer = new SymTypeFreeVariableReplaceVisitor();
    this.symTypeCollectionVisitor = new SymTypeCollectionVisitor();
    this.wildCardCapturer = new WildcardCapturer();
  }

  // methods

  protected SymTypeExpression _replaceTypeVariables(
      SymTypeExpression type,
      Map<SymTypeVariable, ? extends SymTypeExpression> replaceMap
  ) {
    return typeVarReplacer.calculate(type, replaceMap);
  }

  protected SymTypeExpression _replaceInferenceVariables(
      SymTypeExpression type,
      Map<SymTypeInferenceVariable, ? extends SymTypeExpression> replaceMap
  ) {
    return infVarReplacer.calculate(type, replaceMap);
  }

  protected Map<SymTypeVariable, SymTypeInferenceVariable> _getFreeVariableReplaceMap(
      SymTypeExpression type,
      IBasicSymbolsScope enclosingScope
  ) {
    // we are not using the calculated type, this could be optimized.
    return freeVariableReplacer.calculate(type, enclosingScope).getReplaceMap();
  }

  protected List<SymTypeInferenceVariable> _getIncludedInferenceVariables(
      Collection<? extends SymTypeExpression> types
  ) {
    List<SymTypeInferenceVariable> infVars = new ArrayList<>();
    for (SymTypeExpression type : types) {
      infVars.addAll(
          symTypeCollectionVisitor
              .calculate(type, SymTypeExpression::isInferenceVariable)
              .stream()
              .map(SymTypeExpression::asInferenceVariable)
              .toList()
      );
    }
    return infVars;
  }

  protected boolean _hasWildcards(SymTypeExpression type) {
    return !symTypeCollectionVisitor
        .calculate(type, SymTypeExpression::isWildcard)
        .isEmpty();
  }

  protected <T extends SymTypeExpression> T _getCaptureConverted(T type) {
    return wildCardCapturer.getCaptureConverted(type);
  }

}
